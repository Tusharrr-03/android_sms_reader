package com.example.android_sms_reader.parser

object TransactionValidator {
    fun validate(
        senderMatched: Boolean,
        messageType: MessageType,
        transaction: TransactionData,
        originalBody: String,
        amountAmbiguous: Boolean = false
    ): ParseResult {
        if (messageType != MessageType.TRANSACTION) {
            return ParseResult(
                status = ParseStatus.REJECTED,
                messageType = messageType,
                reasons = listOf("Message was not classified as a transaction"),
                originalBody = originalBody
            )
        }

        val reasons = mutableListOf<String>()
        if (!senderMatched) reasons.add("Sender format did not match")
        if (transaction.amount == null || transaction.amount <= 0.0) {
            reasons.add(if (amountAmbiguous) "Transaction amount is ambiguous" else "Missing or invalid amount")
        }
        if (transaction.direction == TransactionDirection.UNKNOWN) {
            reasons.add("Transaction direction is unknown or ambiguous")
        }

        val completeTransaction = transaction.amount != null &&
            transaction.amount > 0.0 &&
            transaction.direction != TransactionDirection.UNKNOWN
        val status = when {
            !completeTransaction -> ParseStatus.NEEDS_REVIEW
            !senderMatched -> ParseStatus.NEEDS_REVIEW
            else -> ParseStatus.AUTO_ACCEPTED
        }
        val score = listOf(
            senderMatched to 20,
            (transaction.amount != null && transaction.amount > 0.0) to 35,
            (transaction.direction != TransactionDirection.UNKNOWN) to 25,
            (transaction.referenceNumber != null) to 10,
            (transaction.accountSuffix != null) to 5,
            (transaction.transactionDateTime != null) to 5
        ).sumOf { (present, points) -> if (present) points else 0 }

        return ParseResult(
            status = status,
            messageType = messageType,
            transaction = transaction,
            score = score,
            reasons = reasons,
            originalBody = originalBody
        )
    }
}