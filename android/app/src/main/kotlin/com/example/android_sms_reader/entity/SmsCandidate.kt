package com.example.android_sms_reader.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(
    tableName = "sms_candidates",
    indices = [
        Index(
            value = ["candidate_key"],
            unique = true
        )
    ]
)
data class SmsCandidate(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "candidate_key")
    val candidateKey: String,

    val sender: String,

    val body: String,

    @ColumnInfo(name = "smsc_time_ms")
    val smscTimeMs: Long,

    val state: String
)