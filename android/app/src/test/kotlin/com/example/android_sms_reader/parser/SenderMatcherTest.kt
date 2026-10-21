package com.example.android_sms_reader.parser

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SenderMatcherTest {
    @Test
    fun matchesIndianSenderFormsCaseInsensitively() {
        assertTrue(SenderMatcher.matches("AB-HDFCBK"))
        assertTrue(SenderMatcher.matches(" vm-hdfcbk-s "))
    }

    @Test
    fun rejectsUnprefixedAndMalformedSenders() {
        assertFalse(SenderMatcher.matches("HDFCBK"))
        assertFalse(SenderMatcher.matches("A-HDFCBK"))
    }
}