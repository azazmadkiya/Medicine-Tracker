package com.example.data.local

import com.example.data.local.entity.Appointment
import com.example.data.local.entity.DoseLog
import com.example.data.local.entity.Medication
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object SampleData {
    fun getTodayString(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    fun getSampleMedications(): List<Medication> {
        return listOf(
            Medication(
                id = 1,
                name = "Amoxicillin",
                dosage = "500 mg",
                form = "Capsule",
                instruction = "After meal with full glass of water",
                frequency = "Twice Daily", frequencyType = "Daily", frequencyData = "", startDate = getTodayString(),
                doseTimes = "08:00,20:00",
                currentStock = 4, // Intentionally low to showcase inventory alert!
                totalPackSize = 20,
                lowStockThreshold = 6,
                unitType = "capsules",
                colorHex = "#E76F51",
                expiryDate = "2026-11-30",
                doctorOrRx = "Dr. Marcus Vance / Rx #784920",
                notes = "Finish complete course of antibiotics",
                isActive = true
            ),
            Medication(
                id = 2,
                name = "Lisinopril",
                dosage = "10 mg",
                form = "Tablet",
                instruction = "Take in morning before breakfast",
                frequency = "Daily", frequencyType = "Daily", frequencyData = "", startDate = getTodayString(),
                doseTimes = "08:00",
                currentStock = 24,
                totalPackSize = 30,
                lowStockThreshold = 7,
                unitType = "pills",
                colorHex = "#006D77",
                expiryDate = "2027-04-15",
                doctorOrRx = "Dr. Rachel Chen / Rx #339102",
                notes = "Monitor blood pressure weekly",
                isActive = true
            ),
            Medication(
                id = 3,
                name = "Metformin",
                dosage = "850 mg",
                form = "Tablet",
                instruction = "With lunch and dinner",
                frequency = "Twice Daily", frequencyType = "Daily", frequencyData = "", startDate = getTodayString(),
                doseTimes = "12:30,19:30",
                currentStock = 42,
                totalPackSize = 60,
                lowStockThreshold = 10,
                unitType = "pills",
                colorHex = "#2A9D8F",
                expiryDate = "2027-08-20",
                doctorOrRx = "Dr. Marcus Vance / Rx #551290",
                notes = "Always take with food to minimize GI discomfort",
                isActive = true
            ),
            Medication(
                id = 4,
                name = "Vitamin D3",
                dosage = "1,000 IU",
                form = "Capsule",
                instruction = "With healthy fats in morning",
                frequency = "Daily", frequencyType = "Daily", frequencyData = "", startDate = getTodayString(),
                doseTimes = "09:00",
                currentStock = 18,
                totalPackSize = 60,
                lowStockThreshold = 7,
                unitType = "capsules",
                colorHex = "#EF6C00",
                expiryDate = "2027-01-10",
                doctorOrRx = "Over the counter",
                notes = "Dietary supplement for bone & immune support",
                isActive = true
            ),
            Medication(
                id = 5,
                name = "Ventolin HFA",
                dosage = "90 mcg",
                form = "Inhaler",
                instruction = "1 puff before bed or as needed",
                frequency = "Daily", frequencyType = "Daily", frequencyData = "", startDate = getTodayString(),
                doseTimes = "21:30",
                currentStock = 14,
                totalPackSize = 200,
                lowStockThreshold = 20,
                unitType = "sprays",
                colorHex = "#1565C0",
                expiryDate = "2026-10-01",
                doctorOrRx = "Dr. Rachel Chen / Rx #992144",
                notes = "Keep inhaler at room temperature",
                isActive = true
            ),
            Medication(
                id = 6,
                name = "Systane Ultra",
                dosage = "1 drop",
                form = "Drops",
                instruction = "1 drop in each eye for dry eyes",
                frequency = "Twice Daily", frequencyType = "Daily", frequencyData = "", startDate = getTodayString(),
                doseTimes = "13:00,21:00",
                currentStock = 2, // Low stock alert!
                totalPackSize = 10,
                lowStockThreshold = 3,
                unitType = "ml",
                colorHex = "#6A1B9A",
                expiryDate = "2026-09-30",
                doctorOrRx = "Dr. Elena Rostova",
                notes = "Discard 30 days after opening",
                isActive = true
            )
        )
    }

    fun getSampleDoseLogs(): List<DoseLog> {
        val today = getTodayString()
        return listOf(
            DoseLog(
                id = 1,
                medicationId = 1,
                doseDate = today,
                doseTime = "08:00",
                status = "TAKEN",
                takenTimestamp = System.currentTimeMillis() - (3 * 3600 * 1000)
            ),
            DoseLog(
                id = 2,
                medicationId = 2,
                doseDate = today,
                doseTime = "08:00",
                status = "TAKEN",
                takenTimestamp = System.currentTimeMillis() - (3 * 3600 * 1000)
            )
        )
    }

    fun getSampleAppointments(): List<Appointment> {
        val now = System.currentTimeMillis()
        val calendar = Calendar.getInstance()

        // Appointment 1: In 2 days at 10:30 AM
        calendar.timeInMillis = now
        calendar.add(Calendar.DAY_OF_YEAR, 2)
        calendar.set(Calendar.HOUR_OF_DAY, 10)
        calendar.set(Calendar.MINUTE, 30)
        calendar.set(Calendar.SECOND, 0)
        val appt1Time = calendar.timeInMillis

        // Appointment 2: In 5 days at 2:15 PM
        calendar.timeInMillis = now
        calendar.add(Calendar.DAY_OF_YEAR, 5)
        calendar.set(Calendar.HOUR_OF_DAY, 14)
        calendar.set(Calendar.MINUTE, 15)
        calendar.set(Calendar.SECOND, 0)
        val appt2Time = calendar.timeInMillis

        // Appointment 3: In 14 days at 9:00 AM
        calendar.timeInMillis = now
        calendar.add(Calendar.DAY_OF_YEAR, 14)
        calendar.set(Calendar.HOUR_OF_DAY, 9)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        val appt3Time = calendar.timeInMillis

        // Appointment 4: Past completed
        calendar.timeInMillis = now
        calendar.add(Calendar.DAY_OF_YEAR, -21)
        calendar.set(Calendar.HOUR_OF_DAY, 11)
        val appt4Time = calendar.timeInMillis

        return listOf(
            Appointment(
                id = 1,
                doctorName = "Dr. Rachel Chen",
                specialty = "Cardiology",
                clinicName = "City Heart & Vascular Center",
                location = "742 Evergreen Medical Park, Suite 300",
                dateTimeMillis = appt1Time,
                reminderMinutesBefore = 60,
                phoneNumber = "(555) 234-8901",
                reasonNotes = "Follow-up blood pressure review & ECG assessment. Fast for 4 hours prior.",
                status = "UPCOMING"
            ),
            Appointment(
                id = 2,
                doctorName = "Dr. Marcus Vance",
                specialty = "Endocrinology",
                clinicName = "Metropolitan Diabetes & Hormone Care",
                location = "410 Health Blvd, Building B",
                dateTimeMillis = appt2Time,
                reminderMinutesBefore = 120,
                phoneNumber = "(555) 876-5432",
                reasonNotes = "Quarterly HbA1c check & Metformin dosage adjustment evaluation.",
                status = "UPCOMING"
            ),
            Appointment(
                id = 3,
                doctorName = "Dr. Elena Rostova",
                specialty = "Ophthalmology",
                clinicName = "ClearVision Eye Clinic",
                location = "1200 Beacon Street, 4th Floor",
                dateTimeMillis = appt3Time,
                reminderMinutesBefore = 60,
                phoneNumber = "(555) 345-6789",
                reasonNotes = "Annual dilated retinal examination and intraocular pressure test.",
                status = "UPCOMING"
            ),
            Appointment(
                id = 4,
                doctorName = "Dr. Keith Howard",
                specialty = "General Dentistry",
                clinicName = "SmileCraft Dental Studio",
                location = "89 Elm Street",
                dateTimeMillis = appt4Time,
                reminderMinutesBefore = 30,
                phoneNumber = "(555) 112-9876",
                reasonNotes = "Routine teeth cleaning and dental hygiene check.",
                status = "COMPLETED"
            )
        )
    }

    fun getSampleContacts(): List<com.example.data.local.entity.Contact> {
        return listOf(
            com.example.data.local.entity.Contact(
                id = 1,
                name = "City Center Pharmacy",
                type = "PHARMACY",
                phoneNumber = "(555) 123-4567",
                address = "123 Main St, Cityville",
                notes = "Open 24/7"
            ),
            com.example.data.local.entity.Contact(
                id = 2,
                name = "General Hospital",
                type = "HOSPITAL",
                phoneNumber = "(555) 987-6543",
                address = "456 Health Way, Cityville",
                notes = "Emergency Room access on the west side"
            ),
            com.example.data.local.entity.Contact(
                id = 3,
                name = "Dr. Sarah Johnson (Cardiology)",
                type = "HOSPITAL",
                phoneNumber = "(555) 555-1234",
                address = "789 Heart Ave, Suite 100",
                notes = "Call during business hours only"
            )
        )
    }
}
