package com.example.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.data.local.entity.Appointment
import com.example.data.local.entity.Medication
import com.example.notifications.receiver.ReminderAlarmReceiver
import java.util.Calendar

class AlarmScheduler(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun scheduleMedicationDose(medication: Medication, doseTime: String) {
        val parts = doseTime.split(":")
        if (parts.size < 2) return

        val hour = parts[0].toIntOrNull() ?: 8
        val minute = parts[1].toIntOrNull() ?: 0

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        val intent = Intent(context, ReminderAlarmReceiver::class.java).apply {
            putExtra(NotificationHelper.EXTRA_TYPE, NotificationHelper.TYPE_MEDICATION)
            putExtra(NotificationHelper.EXTRA_MED_ID, medication.id)
            putExtra(NotificationHelper.EXTRA_DOSE_TIME, doseTime)
        }

        val requestCode = (medication.id * 1000 + hour * 60 + minute).toInt()
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        setExactAlarm(calendar.timeInMillis, pendingIntent)
    }

    fun scheduleNextDailyDose(medication: Medication, doseTime: String) {
        val parts = doseTime.split(":")
        if (parts.size < 2) return

        val hour = parts[0].toIntOrNull() ?: 8
        val minute = parts[1].toIntOrNull() ?: 0

        val calendar = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, 1)
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val intent = Intent(context, ReminderAlarmReceiver::class.java).apply {
            putExtra(NotificationHelper.EXTRA_TYPE, NotificationHelper.TYPE_MEDICATION)
            putExtra(NotificationHelper.EXTRA_MED_ID, medication.id)
            putExtra(NotificationHelper.EXTRA_DOSE_TIME, doseTime)
        }

        val requestCode = (medication.id * 1000 + hour * 60 + minute).toInt()
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        setExactAlarm(calendar.timeInMillis, pendingIntent)
    }

    fun scheduleSnooze(medId: Long, medName: String, doseTime: String, snoozeMinutes: Int = 15) {
        val triggerTime = System.currentTimeMillis() + (snoozeMinutes * 60 * 1000L)
        val intent = Intent(context, ReminderAlarmReceiver::class.java).apply {
            putExtra(NotificationHelper.EXTRA_TYPE, NotificationHelper.TYPE_MEDICATION)
            putExtra(NotificationHelper.EXTRA_MED_ID, medId)
            putExtra(NotificationHelper.EXTRA_MED_NAME, medName)
            putExtra(NotificationHelper.EXTRA_DOSE_TIME, doseTime)
        }
        val requestCode = (medId * 2000).toInt()
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        setExactAlarm(triggerTime, pendingIntent)
    }

    fun scheduleAppointmentReminder(appointment: Appointment) {
        val triggerTime1Hour = appointment.dateTimeMillis - (60 * 60 * 1000L) // 1 hour before
        val triggerTime1Day = appointment.dateTimeMillis - (24 * 60 * 60 * 1000L) // 1 day before
        
        val intent = Intent(context, ReminderAlarmReceiver::class.java).apply {
            putExtra(NotificationHelper.EXTRA_TYPE, NotificationHelper.TYPE_APPOINTMENT)
            putExtra("extra_appt_id", appointment.id)
            putExtra("extra_doctor", appointment.doctorName)
            putExtra("extra_specialty", appointment.specialty)
            putExtra("extra_clinic", appointment.clinicName)
            putExtra("extra_time", "${appointment.formattedDate} at ${appointment.formattedTime}")
        }

        // Schedule 1 hour before
        if (triggerTime1Hour > System.currentTimeMillis()) {
            val requestCode1Hour = (appointment.id * 5000).toInt()
            val pendingIntent1Hour = PendingIntent.getBroadcast(
                context,
                requestCode1Hour,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            setExactAlarm(triggerTime1Hour, pendingIntent1Hour)
        }
        
        // Schedule 1 day before
        if (triggerTime1Day > System.currentTimeMillis()) {
            val requestCode1Day = (appointment.id * 5000 + 1).toInt()
            val pendingIntent1Day = PendingIntent.getBroadcast(
                context,
                requestCode1Day,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            setExactAlarm(triggerTime1Day, pendingIntent1Day)
        }
    }

    fun cancelAppointmentReminder(appointmentId: Long) {
        val intent = Intent(context, ReminderAlarmReceiver::class.java)
        
        // Cancel 1 hour before alarm
        val requestCode1Hour = (appointmentId * 5000).toInt()
        val pendingIntent1Hour = PendingIntent.getBroadcast(
            context,
            requestCode1Hour,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent1Hour != null) {
            alarmManager.cancel(pendingIntent1Hour)
        }
        
        // Cancel 1 day before alarm
        val requestCode1Day = (appointmentId * 5000 + 1).toInt()
        val pendingIntent1Day = PendingIntent.getBroadcast(
            context,
            requestCode1Day,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent1Day != null) {
            alarmManager.cancel(pendingIntent1Day)
        }
    }

    fun cancelMedicationAlarm(medication: Medication, doseTime: String) {
        val parts = doseTime.split(":")
        if (parts.size < 2) return
        val hour = parts[0].toIntOrNull() ?: 8
        val minute = parts[1].toIntOrNull() ?: 0

        val intent = Intent(context, ReminderAlarmReceiver::class.java)
        val requestCode = (medication.id * 1000 + hour * 60 + minute).toInt()
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
        }
    }

    private fun setExactAlarm(triggerAtMillis: Long, pendingIntent: PendingIntent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
            } else {
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
        }
    }
}
