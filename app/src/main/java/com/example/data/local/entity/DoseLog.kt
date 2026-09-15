package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "dose_logs",
    indices = [
        Index(value = ["medicationId", "doseDate", "doseTime"], unique = true)
    ]
)
data class DoseLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val medicationId: Long,
    val doseDate: String, // "YYYY-MM-DD"
    val doseTime: String, // "HH:mm"
    val status: String = "PENDING", // "PENDING", "TAKEN", "SKIPPED"
    val takenTimestamp: Long? = null,
    val notes: String = ""
)
