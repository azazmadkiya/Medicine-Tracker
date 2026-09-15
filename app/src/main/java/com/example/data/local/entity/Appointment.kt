package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Entity(tableName = "appointments")
data class Appointment(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val doctorName: String,
    val specialty: String,
    val clinicName: String,
    val location: String = "",
    val dateTimeMillis: Long,
    val reminderMinutesBefore: Int = 60,
    val phoneNumber: String = "",
    val reasonNotes: String = "",
    val status: String = "UPCOMING" // "UPCOMING", "COMPLETED", "CANCELLED"
) {
    val formattedDate: String
        get() = SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault()).format(Date(dateTimeMillis))

    val formattedTime: String
        get() = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(dateTimeMillis))

    val isUpcoming: Boolean
        get() = status == "UPCOMING" && dateTimeMillis >= System.currentTimeMillis()

    val isPast: Boolean
        get() = dateTimeMillis < System.currentTimeMillis()
}
