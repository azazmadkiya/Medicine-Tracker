package com.example.data.local

import com.example.data.local.entity.Appointment
import com.example.data.local.entity.Contact
import com.example.data.local.entity.DoseLog
import com.example.data.local.entity.Medication

data class BackupData(
    val medications: List<Medication>,
    val doseLogs: List<DoseLog>,
    val appointments: List<Appointment>,
    val contacts: List<Contact>
)
