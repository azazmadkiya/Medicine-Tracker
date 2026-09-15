package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contacts")
data class Contact(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val type: String, // "PHARMACY" or "HOSPITAL"
    val phoneNumber: String,
    val address: String,
    val notes: String = ""
)
