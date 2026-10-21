package com.example.android_sms_reader.parser

import org.junit.Assert.assertEquals
import org.junit.Test

class MessageClassifierTest {
    @Test
    fun classifiesDebitAndCredit() {
        assertEquals(MessageType.TRANSACTION, MessageClassifier.classify("Account debited by INR 500"))
        assertEquals(MessageType.TRANSACTION, MessageClassifier.classify("INR 500 credited to your account"))
    }

    @Test
    fun classifiesOtpAndPromotionWithoutTreatingAmountsAsTransactions() {
        assertEquals(MessageType.OTP, MessageClassifier.classify("Your OTP is INR 500. Do not share it."))
        assertEquals(MessageType.PROMOTIONAL, MessageClassifier.classify("Special offer! Click here to claim a reward."))
    }

    @Test
    fun transactionOutranksUnrelatedPromotionalCopy() {
        assertEquals(
            MessageType.TRANSACTION,
            MessageClassifier.classify("INR 500 debited from account. Earn a special offer today.")
        )
    }

    @Test
    fun classifiesBalanceOnlyAlert() {
        assertEquals(MessageType.BALANCE_ALERT, MessageClassifier.classify("Available balance is INR 500."))
    }
}