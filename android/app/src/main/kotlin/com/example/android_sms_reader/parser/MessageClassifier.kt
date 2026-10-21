package com.example.android_sms_reader.parser

object MessageClassifier {
    private val promotionalPattern = Regex(
        """\b(offer|cashback|subscribe now|limited time|win exciting|click here to claim|special discount)\b""",
        RegexOption.IGNORE_CASE
    )
    private val otpPattern = Regex(
        """\b(otp|one time password|verification code)\b""",
        RegexOption.IGNORE_CASE
    )
    private val strongTransactionPattern = Regex(
        """\b(debited|credited|withdrawn|deposited|spent|purchase|paid|transferred|received)\b""",
        RegexOption.IGNORE_CASE
    )
    private val transactionPattern = Regex(
        """\b(debited|credited|withdrawn|deposited|spent|purchase|paid|transferred|received|upi txn|transaction (?:was )?(?:successful|completed))\b""",
        RegexOption.IGNORE_CASE
    )
    private val balancePattern = Regex(
        """\b(available|current|closing|ledger)?\s*balance\s*(is|of|:|=)?\b""",
        RegexOption.IGNORE_CASE
    )

    fun classify(message: String): MessageType {
        val text = SmsNormalizer.normalize(message)
        val hasTransaction = transactionPattern.containsMatchIn(text)

        if (otpPattern.containsMatchIn(text) && !strongTransactionPattern.containsMatchIn(text)) {
            return MessageType.OTP
        }
        if (hasTransaction) return MessageType.TRANSACTION
        if (balancePattern.containsMatchIn(text)) return MessageType.BALANCE_ALERT
        if (promotionalPattern.containsMatchIn(text)) return MessageType.PROMOTIONAL
        return MessageType.UNKNOWN
    }
}