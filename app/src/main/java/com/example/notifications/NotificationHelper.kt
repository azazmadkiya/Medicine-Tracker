package com.example.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import com.example.notifications.receiver.ReminderAlarmReceiver

class NotificationHelper(private val context: Context) {

    companion object {
        const val CHANNEL_MEDICATION = "medication_reminders_channel_v2"
        const val CHANNEL_LOW_STOCK = "low_stock_alerts_channel_v2"
        const val CHANNEL_APPOINTMENT = "appointment_reminders_channel_v2"

        const val EXTRA_MED_ID = "extra_med_id"
        const val EXTRA_MED_NAME = "extra_med_name"
        const val EXTRA_DOSE_TIME = "extra_dose_time"
        const val EXTRA_SNOOZE_DURATION = "extra_snooze_duration"
        const val EXTRA_TYPE = "extra_type"

        const val TYPE_MEDICATION = "TYPE_MEDICATION"
        const val TYPE_APPOINTMENT = "TYPE_APPOINTMENT"
        const val TYPE_LOW_STOCK = "TYPE_LOW_STOCK"

        const val ACTION_MARK_TAKEN = "com.example.ACTION_MARK_TAKEN"
        const val ACTION_SNOOZE = "com.example.ACTION_SNOOZE"
    }

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.packageName + "/" + R.raw.medication_alert)
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build()

            val medChannel = NotificationChannel(
                CHANNEL_MEDICATION,
                "Medication Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifies when it is time to take prescribed medications and doses"
                enableVibration(true)
                enableLights(true)
                setSound(soundUri, audioAttributes)
            }

            val stockChannel = NotificationChannel(
                CHANNEL_LOW_STOCK,
                "Medication Inventory Alerts",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Alerts when medicine stock is running low and needs refilling"
                setSound(soundUri, audioAttributes)
            }

            val apptChannel = NotificationChannel(
                CHANNEL_APPOINTMENT,
                "Doctor Appointment Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Reminders for upcoming clinical visits and doctor consultations"
                enableVibration(true)
                setSound(soundUri, audioAttributes)
            }

            notificationManager.createNotificationChannels(listOf(medChannel, stockChannel, apptChannel))
        }
    }

    fun showMedicationNotification(
        medicationId: Long,
        medName: String,
        dosage: String,
        instruction: String,
        doseTime: String
    ) {
        val tapIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val tapPendingIntent = PendingIntent.getActivity(
            context,
            medicationId.toInt() * 100,
            tapIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Action: Taken
        val takenIntent = Intent(context, ReminderAlarmReceiver::class.java).apply {
            action = ACTION_MARK_TAKEN
            putExtra(EXTRA_MED_ID, medicationId)
            putExtra(EXTRA_DOSE_TIME, doseTime)
        }
        val takenPendingIntent = PendingIntent.getBroadcast(
            context,
            medicationId.toInt() * 100 + 1,
            takenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Action: Snooze (10 min)
        val snooze10Intent = Intent(context, ReminderAlarmReceiver::class.java).apply {
            action = ACTION_SNOOZE
            putExtra(EXTRA_MED_ID, medicationId)
            putExtra(EXTRA_MED_NAME, medName)
            putExtra(EXTRA_DOSE_TIME, doseTime)
            putExtra(EXTRA_SNOOZE_DURATION, 10)
        }
        val snooze10PendingIntent = PendingIntent.getBroadcast(
            context,
            medicationId.toInt() * 100 + 2,
            snooze10Intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Action: Snooze (30 min)
        val snooze30Intent = Intent(context, ReminderAlarmReceiver::class.java).apply {
            action = ACTION_SNOOZE
            putExtra(EXTRA_MED_ID, medicationId)
            putExtra(EXTRA_MED_NAME, medName)
            putExtra(EXTRA_DOSE_TIME, doseTime)
            putExtra(EXTRA_SNOOZE_DURATION, 30)
        }
        val snooze30PendingIntent = PendingIntent.getBroadcast(
            context,
            medicationId.toInt() * 100 + 3,
            snooze30Intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_MEDICATION)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("Time to take $medName ($dosage)")
            .setContentText(instruction.ifBlank { "Scheduled dose for $doseTime" })
            .setStyle(NotificationCompat.BigTextStyle().bigText("Time to take $medName $dosage.\nInstructions: $instruction\nScheduled for $doseTime."))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(tapPendingIntent)
            .setAutoCancel(true)
            .addAction(android.R.drawable.checkbox_on_background, "Mark Taken", takenPendingIntent)
            .addAction(android.R.drawable.ic_popup_sync, "Snooze 10m", snooze10PendingIntent)
            .addAction(android.R.drawable.ic_popup_sync, "Snooze 30m", snooze30PendingIntent)

        notificationManager.notify((medicationId * 100).toInt(), builder.build())
    }

    fun showLowStockNotification(
        medicationId: Long,
        medName: String,
        stockLeft: Int,
        unit: String
    ) {
        val tapIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val tapPendingIntent = PendingIntent.getActivity(
            context,
            (medicationId * 1000).toInt(),
            tapIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_LOW_STOCK)
            .setSmallIcon(android.R.drawable.stat_notify_error)
            .setContentTitle("Low Stock Alert: $medName")
            .setContentText("Only $stockLeft $unit remaining. Tap to request refill.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(tapPendingIntent)
            .setAutoCancel(true)

        notificationManager.notify((medicationId * 1000).toInt(), builder.build())
    }

    fun showAppointmentNotification(
        appointmentId: Long,
        doctorName: String,
        specialty: String,
        clinicName: String,
        timeFormatted: String
    ) {
        val tapIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val tapPendingIntent = PendingIntent.getActivity(
            context,
            (appointmentId * 500).toInt(),
            tapIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_APPOINTMENT)
            .setSmallIcon(android.R.drawable.ic_menu_my_calendar)
            .setContentTitle("Upcoming Appointment: $doctorName")
            .setContentText("$specialty at $clinicName ($timeFormatted)")
            .setStyle(NotificationCompat.BigTextStyle().bigText("Upcoming clinical visit with $doctorName ($specialty)\nLocation: $clinicName\nTime: $timeFormatted"))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(tapPendingIntent)
            .setAutoCancel(true)

        notificationManager.notify((appointmentId * 500).toInt(), builder.build())
    }
}
