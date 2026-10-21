package com.example.android_sms_reader.parser

import java.text.Normalizer

object SmsNormalizer {
    fun normalize(body: String): String = Normalizer
        .normalize(body, Normalizer.Form.NFKC)
        .replace('\u00A0', ' ')
        .replace(Regex("\\s+"), " ")
        .trim()
}