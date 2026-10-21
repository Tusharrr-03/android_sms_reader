package com.example.android_sms_reader.parser

object SenderMatcher {
    private val senderPattern = Regex(
        "^[A-Z]{2}-[A-Z0-9]{3,10}(?:-[A-Z])?$",
        RegexOption.IGNORE_CASE
    )

    fun matches(sender: String): Boolean = senderPattern.matches(sender.trim())
}