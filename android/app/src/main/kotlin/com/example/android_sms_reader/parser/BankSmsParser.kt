package com.example.android_sms_reader.parser

class BankSmsParser {
    fun parse(input: SmsInput): ParseResult {
        val originalBody = input.body
        val normalizedBody = SmsNormalizer.normalize(originalBody)
        val messageType = MessageClassifier.classify(normalizedBody)
        if (normalizedBody.isEmpty()) {
            return ParseResult(
                status = ParseStatus.REJECTED,
                messageType = MessageType.UNKNOWN,
                reasons = listOf("Message body is empty"),
                originalBody = originalBody
            )
        }

        val amountResult = AmountExtractor.extract(normalizedBody)
        val reference = ReferenceExtractor.extract(originalBody)
        val transaction = TransactionData(
            amount = amountResult.amount,
            direction = amountResult.direction,
            referenceNumber = reference.referenceNumber,
            accountSuffix = reference.accountSuffix,
            transactionDateTime = reference.transactionDateTime
        )
        return TransactionValidator.validate(
            senderMatched = SenderMatcher.matches(input.sender),
            messageType = messageType,
            transaction = transaction,
            originalBody = originalBody,
            amountAmbiguous = amountResult.isAmbiguous
        )
    }
}