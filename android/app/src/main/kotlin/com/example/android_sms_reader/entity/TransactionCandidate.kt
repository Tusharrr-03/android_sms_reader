package com.example.android_sms_reader.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "transaction_candidates",
    indices = [
        Index(
            value = ["fingerprint"],
            unique = true
        )
    ]
)
data class TransactionCandidate(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "amount_minor")
    val amountMinor: Long? = null,

    val direction: String,

    @ColumnInfo(name = "reference_number")
    val referenceNumber: String? = null,

    val fingerprint: String,

    @ColumnInfo(name = "verification_status")
    val verificationStatus: String,

    @ColumnInfo(name = "transaction_time_ms")
    val transactionTimeMs: Long? = null
)
