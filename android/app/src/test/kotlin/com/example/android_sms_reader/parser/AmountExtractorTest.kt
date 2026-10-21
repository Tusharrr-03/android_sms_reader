package com.example.android_sms_reader.parser

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AmountExtractorTest {
    @Test
    fun extractsRupeeAmount() {
        val result = AmountExtractor.extract("Your account is debited by INR 500.")
        assertEquals(500.0, result.amount ?: -1.0, 0.001)
        assertEquals(TransactionDirection.DEBIT, result.direction)
    }

    @Test
    fun extractsCurrencySymbolAndIndianGrouping() {
        val result = AmountExtractor.extract("Rs. 1,23,456.50 was credited to your account.")
        assertEquals(123456.50, result.amount ?: -1.0, 0.001)
        assertEquals(TransactionDirection.CREDIT, result.direction)
    }

    @Test
    fun choosesTransactionAmountOverAvailableBalance() {
        val result = AmountExtractor.extract(
            "INR 500 was debited. Available balance is INR 4,500."
        )
        assertEquals(500.0, result.amount ?: -1.0, 0.001)
    }

    @Test
    fun doesNotGuessWhenMultipleAmountsAreUnqualified() {
        val result = AmountExtractor.extract("Transaction completed: INR 500 and INR 700.")
        assertNull(result.amount)
        assertEquals(true, result.isAmbiguous)
    }

    @Test
    fun excludesBalanceOnlyAmounts() {
        val result = AmountExtractor.extract("Available balance is INR 500.")
        assertNull(result.amount)
    }
}