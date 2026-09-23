package com.example.notifications.receiver

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import com.example.data.local.AppDatabase
import com.example.data.local.SampleData
import com.example.data.repository.MedicationRepository
import com.example.notifications.AlarmScheduler
import com.example.notifications.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ReminderAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val pendingResult = goAsync()
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "MedicineTracker:ReminderWakeLock"
        )
        wakeLock.acquire(10 * 60 * 1000L) // 10 minutes max timeout

        val action = intent.action
        val notificationHelper = NotificationHelper(context)
        val scope = CoroutineScope(Dispatchers.IO)

        scope.launch {
            try {
                when (action) {
                    NotificationHelper.ACTION_MARK_TAKEN -> {
                        val medId = intent.getLongExtra(NotificationHelper.EXTRA_MED_ID, -1L)
                        val doseTime = intent.getStringExtra(NotificationHelper.EXTRA_DOSE_TIME) ?: ""
                        val notifManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                        if (medId != -1L) {
                            notifManager.cancel((medId * 100).toInt())
                            val db = AppDatabase.getDatabase(context)
                            val repo = MedicationRepository(db.medicationDao(), db.doseLogDao())
                            val today = SampleData.getTodayString()
                            val newlyLowStockMed = repo.markDoseTaken(medId, today, doseTime)
                            if (newlyLowStockMed != null) {
                                notificationHelper.showLowStockNotification(
                                    medicationId = newlyLowStockMed.id,
                                    medName = newlyLowStockMed.name,
                                    stockLeft = newlyLowStockMed.currentStock,
                                    unit = newlyLowStockMed.unitType
                                )
                            }
                        }
                    }
                    NotificationHelper.ACTION_SNOOZE -> {
                        val medId = intent.getLongExtra(NotificationHelper.EXTRA_MED_ID, -1L)
                        val medName = intent.getStringExtra(NotificationHelper.EXTRA_MED_NAME) ?: "Medication"
                        val doseTime = intent.getStringExtra(NotificationHelper.EXTRA_DOSE_TIME) ?: ""
                        val duration = intent.getIntExtra(NotificationHelper.EXTRA_SNOOZE_DURATION, 10)
                        val notifManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                        if (medId != -1L) {
                            notifManager.cancel((medId * 100).toInt())
                            val alarmScheduler = AlarmScheduler(context)
                            alarmScheduler.scheduleSnooze(medId, medName, doseTime, duration)
                        }
                    }
                    else -> {
                        val type = intent.getStringExtra(NotificationHelper.EXTRA_TYPE) ?: NotificationHelper.TYPE_MEDICATION
                        val medId = intent.getLongExtra(NotificationHelper.EXTRA_MED_ID, -1L)
                        if (type == NotificationHelper.TYPE_MEDICATION && medId != -1L) {
                            val db = AppDatabase.getDatabase(context)
                            val med = db.medicationDao().getMedicationDirect(medId)
                            if (med != null && med.isActive) {
                                val doseTime = intent.getStringExtra(NotificationHelper.EXTRA_DOSE_TIME) ?: "08:00"
                                notificationHelper.showMedicationNotification(
                                    medicationId = med.id,
                                    medName = med.name,
                                    dosage = med.dosage,
                                    instruction = med.instruction,
                                    doseTime = doseTime
                                )
                                val alarmScheduler = AlarmScheduler(context)
                                alarmScheduler.scheduleNextDailyDose(med, doseTime)

                                if (med.isLowStock) {
                                    notificationHelper.showLowStockNotification(
                                        medicationId = med.id,
                                        medName = med.name,
                                        stockLeft = med.currentStock,
                                        unit = med.unitType
                                    )
                                }
                            }
                        } else if (type == NotificationHelper.TYPE_APPOINTMENT) {
                            val apptId = intent.getLongExtra("extra_appt_id", -1L)
                            val doctor = intent.getStringExtra("extra_doctor") ?: "Doctor"
                            val specialty = intent.getStringExtra("extra_specialty") ?: "Specialist"
                            val clinic = intent.getStringExtra("extra_clinic") ?: "Clinic"
                            val time = intent.getStringExtra("extra_time") ?: "Upcoming"
                            notificationHelper.showAppointmentNotification(
                                appointmentId = apptId,
                                doctorName = doctor,
                                specialty = specialty,
                                clinicName = clinic,
                                timeFormatted = time
                            )
                        }
                    }
                }
            } finally {
                if (wakeLock.isHeld) {
                    wakeLock.release()
                }
                pendingResult.finish()
            }
        }
    }
}
