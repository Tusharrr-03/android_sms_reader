package com.example.android_sms_reader.parser

data class ExtractedReference(
    val referenceNumber: String?,
    val accountSuffix: String?,
    val transactionDateTime: String?
)

object ReferenceExtractor {
    private val referencePatterns = listOf(
        Regex(
            """\b(?:UTR|RRN|UPI\s*REF(?:ERENCE)?|REF(?:ERENCE)?\s*(?:NO|NUMBER)?|TXN\s*ID|TRANSACTION\s*ID)\s*[:#-]?\s*([A-Z0-9]{6,30})\b""",
            RegexOption.IGNORE_CASE
        )
    )
    private val accountPattern = Regex(
        """\b(?:a/c|account|acct)\s*(?:no\.?|number)?\s*[:#-]?\s*(?:x{2,}|\*{2,})?([0-9]{4})\b""",
        RegexOption.IGNORE_CASE
    )
    private val dateTimePattern = Regex(
        """\b(\d{1,2}[-/]\d{1,2}[-/]\d{2,4}(?:\s+\d{1,2}:\d{2}(?::\d{2})?)?)\b"""
    )

    fun extract(message: String): ExtractedReference {
        val reference = referencePatterns.firstNotNullOfOrNull { pattern ->
            pattern.find(message)?.groupValues?.get(1)
        }
        val accountSuffix = accountPattern.find(message)?.groupValues?.get(1)
        val dateTime = dateTimePattern.find(message)?.groupValues?.get(1)
        return ExtractedReference(reference, accountSuffix, dateTime)
    }
}