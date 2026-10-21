package com.example.android_sms_reader.parser

data class AmountExtractionResult(
    val amount: Double?,
    val direction: TransactionDirection,
    val isAmbiguous: Boolean = false
)

object AmountExtractor {
    private val amountPattern = Regex(
        """(?:₹|rs\.?|inr)\s*((?:\d{1,3}(?:,\d{2})*,\d{3}|\d+)(?:\.\d{1,2})?)""",
        RegexOption.IGNORE_CASE
    )
    private val debitPattern = Regex(
        """\b(debited|withdrawn|spent|paid|purchase|transferred|sent)\b""",
        RegexOption.IGNORE_CASE
    )
    private val creditPattern = Regex(
        """\b(credited|deposited|received|refunded)\b""",
        RegexOption.IGNORE_CASE
    )
    private val balancePattern = Regex(
        """\b(balance|available|closing|ledger)\b""",
        RegexOption.IGNORE_CASE
    )
    private val transactionContextPattern = Regex(
        """\b(debited|credited|withdrawn|deposited|spent|paid|purchase|transferred|received|refunded)\b""",
        RegexOption.IGNORE_CASE
    )

    fun extract(text: String): AmountExtractionResult {
        val candidates = amountPattern.findAll(text).map { match ->
            val value = match.groupValues[1].replace(",", "").toDoubleOrNull()
            val start = match.range.first
            val end = match.range.last + 1
            val before = text.substring((start - 48).coerceAtLeast(0), start)
            val after = text.substring(end, (end + 36).coerceAtMost(text.length))
            val balanceDistance = nearestDistance(balancePattern, before, after)
            val transactionDistance = nearestDistance(transactionContextPattern, before, after)
            val contextScore = when {
                balanceDistance != null &&
                    (transactionDistance == null || balanceDistance < transactionDistance) -> -100
                transactionDistance != null -> 10
                else -> 0
            }
            Candidate(value, contextScore)
        }.filter { it.value != null }.toList()

        val direction = direction(text)
        val eligible = candidates.filter { it.contextScore >= 0 }
        if (eligible.isEmpty()) return AmountExtractionResult(null, direction)

        val bestScore = eligible.maxOf { it.contextScore }
        val best = eligible.filter { it.contextScore == bestScore }
        if (best.size > 1) return AmountExtractionResult(null, direction, isAmbiguous = true)
        if (bestScore == 0 && eligible.size > 1) {
            return AmountExtractionResult(null, direction, isAmbiguous = true)
        }

        return AmountExtractionResult(best.single().value, direction)
    }

    private fun direction(text: String): TransactionDirection {
        val hasDebit = debitPattern.containsMatchIn(text)
        val hasCredit = creditPattern.containsMatchIn(text)
        return when {
            hasDebit == hasCredit -> TransactionDirection.UNKNOWN
            hasDebit -> TransactionDirection.DEBIT
            else -> TransactionDirection.CREDIT
        }
    }

    private fun nearestDistance(pattern: Regex, before: String, after: String): Int? {
        val beforeMatch = pattern.findAll(before).lastOrNull()
        val afterMatch = pattern.find(after)
        val beforeDistance = beforeMatch?.let { before.length - it.range.last }
        val afterDistance = afterMatch?.range?.first?.plus(1)
        return listOfNotNull(beforeDistance, afterDistance).minOrNull()
    }

    private data class Candidate(val value: Double?, val contextScore: Int)
}