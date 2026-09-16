package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contacts")
data class Contact(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val type: String, // "PHARMACY", "HOSPITAL", "DOCTOR"
    val phoneNumber: String = "",
    val email: String = "",
    val website: String = "",
    val speciality: String = "",
    val address: String = "", // Used for Street
    val postcode: String = "",
    val city: String = "",
    val notes: String = ""
)
