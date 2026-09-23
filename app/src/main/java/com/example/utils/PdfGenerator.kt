package com.example.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import com.example.data.local.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfGenerator {
    suspend fun generateReport(context: Context, uri: Uri): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val db = AppDatabase.getDatabase(context)
            val medications = db.medicationDao().getAllMedicationsSync().filter { it.isActive }
            val appointments = db.appointmentDao().getAllAppointmentsSync().filter { 
                it.status == "UPCOMING" && it.dateTimeMillis >= System.currentTimeMillis() 
            }.sortedBy { it.dateTimeMillis }

            val pdfDocument = PdfDocument()
            val paint = Paint()
            val titlePaint = Paint()

            // Page description: A4 size is roughly 595 x 842 at 72 PPI
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
            var page = pdfDocument.startPage(pageInfo)
            var canvas = page.canvas
            var yPosition = 50f
            val margin = 50f
            val lineHeight = 20f

            titlePaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            titlePaint.textSize = 24f
            titlePaint.color = Color.BLACK

            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            paint.textSize = 14f
            paint.color = Color.BLACK

            val dateStr = SimpleDateFormat("MMM dd, yyyy - HH:mm", Locale.getDefault()).format(Date())

            canvas.drawText("Medical Schedule Report", margin, yPosition, titlePaint)
            yPosition += 30f
            paint.textSize = 12f
            paint.color = Color.DKGRAY
            canvas.drawText("Generated on: $dateStr", margin, yPosition, paint)
            yPosition += 40f
            paint.color = Color.BLACK

            // Medications Section
            titlePaint.textSize = 18f
            canvas.drawText("Current Medications", margin, yPosition, titlePaint)
            yPosition += 30f

            paint.textSize = 12f
            if (medications.isEmpty()) {
                canvas.drawText("No active medications.", margin, yPosition, paint)
                yPosition += lineHeight
            } else {
                for (med in medications) {
                    if (yPosition > 780) { // New page needed
                        pdfDocument.finishPage(page)
                        page = pdfDocument.startPage(pageInfo)
                        canvas = page.canvas
                        yPosition = 50f
                    }
                    val times = if (med.doseTimes.isNotBlank()) "at ${med.doseTimes}" else ""
                    val text = "• ${med.name} - ${med.dosage} (${med.form})"
                    val subText = "   Take: ${med.frequencyType} ${if (med.frequencyData.isNotBlank()) "("+med.frequencyData+")" else ""} $times - ${med.instruction}"
                    
                    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    canvas.drawText(text, margin, yPosition, paint)
                    yPosition += 16f
                    
                    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                    canvas.drawText(subText, margin, yPosition, paint)
                    yPosition += lineHeight + 5f
                }
            }

            yPosition += 20f
            if (yPosition > 700) {
                pdfDocument.finishPage(page)
                page = pdfDocument.startPage(pageInfo)
                canvas = page.canvas
                yPosition = 50f
            }

            // Appointments Section
            canvas.drawText("Upcoming Appointments", margin, yPosition, titlePaint)
            yPosition += 30f

            if (appointments.isEmpty()) {
                canvas.drawText("No upcoming appointments.", margin, yPosition, paint)
                yPosition += lineHeight
            } else {
                val dateFormat = SimpleDateFormat("EEE, MMM d, yyyy 'at' h:mm a", Locale.getDefault())
                for (appt in appointments) {
                    if (yPosition > 780) { // New page needed
                        pdfDocument.finishPage(page)
                        page = pdfDocument.startPage(pageInfo)
                        canvas = page.canvas
                        yPosition = 50f
                    }
                    val text = "• ${appt.doctorName} (${appt.specialty})"
                    val subText = "   Date: ${dateFormat.format(Date(appt.dateTimeMillis))}"
                    val locText = "   Location: ${appt.clinicName}"
                    
                    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    canvas.drawText(text, margin, yPosition, paint)
                    yPosition += 16f
                    
                    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                    canvas.drawText(subText, margin, yPosition, paint)
                    yPosition += 16f
                    canvas.drawText(locText, margin, yPosition, paint)
                    yPosition += lineHeight + 5f
                }
            }

            pdfDocument.finishPage(page)

            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                pdfDocument.writeTo(outputStream)
            } ?: return@withContext Result.failure(Exception("Cannot open file for writing"))

            pdfDocument.close()
            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}
