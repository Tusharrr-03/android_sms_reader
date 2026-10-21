package com.example.android_sms_reader.parser

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class BankSmsParserTest {
    private val parser = BankSmsParser()

    @Test
    fun parsesDebitAndPreservesOriginalBody() {
        val original = "Your A/c XX1234 is debited by INR 500. UTR: ABC123456789"
        val result = parser.parse(SmsInput("AB-HDFCBK", original))
        assertEquals(ParseStatus.AUTO_ACCEPTED, result.status)
        assertEquals(TransactionDirection.DEBIT, result.transaction?.direction)
        assertEquals(500.0, result.transaction?.amount)
        assertEquals("ABC123456789", result.transaction?.referenceNumber)
        assertEquals("1234", result.transaction?.accountSuffix)
        assertEquals(original, result.originalBody)
    }

    @Test
    fun rejectsOtpEvenWhenItContainsCurrency() {
        val result = parser.parse(SmsInput("AB-HDFCBK", "Your OTP is INR 500. Do not share it."))
        assertEquals(ParseStatus.REJECTED, result.status)
        assertEquals(MessageType.OTP, result.messageType)
        assertNull(result.transaction)
    }

    @Test
    fun reviewsUnknownSenderAndMissingReferenceIsAllowed() {
        val result = parser.parse(SmsInput("unknown", "Your account is debited by INR 500."))
        assertEquals(ParseStatus.NEEDS_REVIEW, result.status)
        assertNotNull(result.transaction)
        assertNull(result.transaction?.referenceNumber)
    }

    @Test
    fun reviewsTransactionWithoutAnAmountOrDirection() {
        val result = parser.parse(SmsInput("AB-HDFCBK", "A transaction was completed."))
        assertEquals(ParseStatus.NEEDS_REVIEW, result.status)
        assertNull(result.transaction?.amount)
        assertEquals(TransactionDirection.UNKNOWN, result.transaction?.direction)
    }

    @Test
    fun rejectsEmptyBody() {
        val result = parser.parse(SmsInput("AB-HDFCBK", "  \u00A0 "))
        assertEquals(ParseStatus.REJECTED, result.status)
        assertEquals(MessageType.UNKNOWN, result.messageType)
    }

    @Test
    fun ambiguousDirectionRequiresReview() {
        val result = parser.parse(
            SmsInput("AB-HDFCBK", "INR 500 was debited and INR 200 was credited.")
        )
        assertEquals(ParseStatus.NEEDS_REVIEW, result.status)
        assertEquals(TransactionDirection.UNKNOWN, result.transaction?.direction)
    }

    @Test
    fun balanceAmountDoesNotBecomeTransactionAmount() {
        val result = parser.parse(
            SmsInput("AB-HDFCBK", "INR 500 debited. Available balance is INR 4,500.")
        )
        assertEquals(500.0, result.transaction?.amount)
    }
}