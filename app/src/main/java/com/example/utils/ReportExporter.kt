package com.example.utils

import android.content.Context
import android.content.Intent
import com.example.data.local.entity.Appointment
import com.example.ui.viewmodel.MedicationHistory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ReportExporter {
    fun exportReport(
        context: Context,
        historyList: List<MedicationHistory>,
        appointments: List<Appointment>
    ) {
        val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val today = sdf.format(Date())

        val sb = StringBuilder()
        sb.append("Patient Health Report\n")
        sb.append("Generated on: $today\n\n")

        sb.append("=== MEDICATION HISTORY (Last 7 Days) ===\n")
        if (historyList.isEmpty()) {
            sb.append("No active medications tracked.\n")
        } else {
            historyList.forEach { history ->
                sb.append("\nMedication: ${history.medication.name} (${history.medication.dosage})\n")
                sb.append("Instructions: ${history.medication.instruction}\n")
                
                val takenCount = history.historyDays.count { it.isTaken }
                val scheduledCount = history.historyDays.count { it.isScheduled }
                sb.append("Adherence: $takenCount/$scheduledCount doses taken\n")
                
                sb.append("Daily Log:\n")
                // Reversing to show oldest to newest, or keeping as is (oldest first usually)
                history.historyDays.forEach { day ->
                    if (day.isScheduled) {
                        val status = if (day.isTaken) "TAKEN" else "MISSED/PENDING"
                        sb.append("  - ${day.dateString} (${day.dayOfWeek}): $status\n")
                    }
                }
            }
        }

        sb.append("\n\n=== UPCOMING APPOINTMENTS ===\n")
        val upcomingAppts = appointments.filter { it.dateTimeMillis > System.currentTimeMillis() }
        if (upcomingAppts.isEmpty()) {
            sb.append("No upcoming appointments.\n")
        } else {
            upcomingAppts.forEach { appt ->
                val date = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(appt.dateTimeMillis))
                val time = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(appt.dateTimeMillis))
                sb.append("\nDoctor: ${appt.doctorName} (${appt.specialty})\n")
                sb.append("Location: ${appt.clinicName}\n")
                sb.append("When: $date at $time\n")
                if (appt.reasonNotes.isNotBlank()) {
                    sb.append("Notes: ${appt.reasonNotes}\n")
                }
            }
        }

        val reportText = sb.toString()

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, reportText)
            putExtra(Intent.EXTRA_TITLE, "Patient Health Report")
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, "Export Health Report")
        shareIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(shareIntent)
    }
}
