package com.example.android_sms_reader.parser

enum class ParseStatus {
    AUTO_ACCEPTED,
    NEEDS_REVIEW,
    REJECTED
}

enum class TransactionDirection {
    DEBIT,
    CREDIT,
    UNKNOWN
}

enum class MessageType {
    TRANSACTION,
    OTP,
    PROMOTIONAL,
    BALANCE_ALERT,
    UNKNOWN
}

data class SmsInput(
    val sender: String,
    val body: String,
    val receivedAtMillis: Long? = null
)

data class TransactionData(
    val amount: Double? = null,
    val direction: TransactionDirection = TransactionDirection.UNKNOWN,
    val referenceNumber: String? = null,
    val accountSuffix: String? = null,
    val transactionDateTime: String? = null
)

data class ParseResult(
    val status: ParseStatus,
    val messageType: MessageType,
    val transaction: TransactionData? = null,
    val score: Int = 0,
    val reasons: List<String> = emptyList(),
    val originalBody: String
)