package com.example.android_sms_reader.parser

import org.junit.Assert.assertEquals
import org.junit.Test

class SmsNormalizerTest {
    @Test
    fun normalizesUnicodeAndWhitespaceWithoutDiscardingIdentifiers() {
        assertEquals(
            "Rs. 500 REF AbC12345",
            SmsNormalizer.normalize(" Rs.\u00A0  500\nREF   AbC12345 ")
        )
        assertEquals("INR 500", SmsNormalizer.normalize("ＩＮＲ ５００"))
    }
}