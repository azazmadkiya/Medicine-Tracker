package com.example.data.repository

import com.example.data.local.dao.DoseLogDao
import com.example.data.local.dao.MedicationDao
import com.example.data.local.entity.DoseLog
import com.example.data.local.entity.Medication
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

data class DoseWithMedication(
    val medication: Medication,
    val scheduledTime: String, // HH:mm
    val scheduledDate: String, // YYYY-MM-DD
    val doseLog: DoseLog?,
    val status: String // "TAKEN", "SKIPPED", "PENDING"
)

class MedicationRepository(
    private val medicationDao: MedicationDao,
    private val doseLogDao: DoseLogDao
) {
    val allActiveMedications: Flow<List<Medication>> = medicationDao.getAllActiveMedications()
    val allMedications: Flow<List<Medication>> = medicationDao.getAllMedications()
    val lowStockMedications: Flow<List<Medication>> = medicationDao.getLowStockMedications()

    fun getMedicationById(id: Long): Flow<Medication?> = medicationDao.getMedicationById(id)

    suspend fun getMedicationDirect(id: Long): Medication? = medicationDao.getMedicationDirect(id)

    suspend fun addMedication(medication: Medication): Long = medicationDao.insertMedication(medication)

    suspend fun updateMedication(medication: Medication) = medicationDao.updateMedication(medication)

    suspend fun deleteMedication(medication: Medication) {
        doseLogDao.deleteLogsForMedication(medication.id)
        medicationDao.deleteMedication(medication)
    }

    suspend fun addStock(medicationId: Long, amount: Int) {
        medicationDao.addStock(medicationId, amount)
    }

    suspend fun setStock(medicationId: Long, newStock: Int) {
        medicationDao.setStock(medicationId, newStock)
    }

    fun getLogsForDateRange(startDate: String, endDate: String): Flow<List<DoseLog>> {
        return doseLogDao.getLogsForDateRange(startDate, endDate)
    }

    fun getLogsForMedication(medicationId: Long): Flow<List<DoseLog>> {
        return doseLogDao.getLogsForMedication(medicationId)
    }

    fun isScheduledForDate(med: Medication, dateString: String): Boolean {
        if (med.frequencyType == "OnDemand") return false
        if (med.frequencyType == "Daily") return true
        
        try {
            val format = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
            val targetDate = format.parse(dateString) ?: return false
            
            when (med.frequencyType) {
                "SpecificDays" -> {
                    val calendar = java.util.Calendar.getInstance().apply { time = targetDate }
                    val dayOfWeek = calendar.get(java.util.Calendar.DAY_OF_WEEK)
                    val dayStr = when(dayOfWeek) {
                        java.util.Calendar.MONDAY -> "Mon"
                        java.util.Calendar.TUESDAY -> "Tue"
                        java.util.Calendar.WEDNESDAY -> "Wed"
                        java.util.Calendar.THURSDAY -> "Thu"
                        java.util.Calendar.FRIDAY -> "Fri"
                        java.util.Calendar.SATURDAY -> "Sat"
                        java.util.Calendar.SUNDAY -> "Sun"
                        else -> ""
                    }
                    return med.frequencyData.contains(dayStr)
                }
                "Interval" -> {
                    val startDate = format.parse(med.startDate) ?: return false
                    val diff = targetDate.time - startDate.time
                    if (diff < 0) return false
                    val daysDiff = java.util.concurrent.TimeUnit.DAYS.convert(diff, java.util.concurrent.TimeUnit.MILLISECONDS)
                    val interval = med.frequencyData.toLongOrNull() ?: 1L
                    return daysDiff % interval == 0L
                }
                "Cyclic" -> {
                    val startDate = format.parse(med.startDate) ?: return false
                    val diff = targetDate.time - startDate.time
                    if (diff < 0) return false
                    val daysDiff = java.util.concurrent.TimeUnit.DAYS.convert(diff, java.util.concurrent.TimeUnit.MILLISECONDS)
                    val parts = med.frequencyData.split(",")
                    val intakeDays = parts.getOrNull(0)?.toLongOrNull() ?: 21L
                    val pauseDays = parts.getOrNull(1)?.toLongOrNull() ?: 7L
                    val cycleLength = intakeDays + pauseDays
                    val dayInCycle = daysDiff % cycleLength
                    return dayInCycle < intakeDays
                }
                else -> return true
            }
        } catch (e: Exception) {
            return true // Fallback to daily if error
        }
    }

    fun getDosesForDate(date: String): Flow<List<DoseWithMedication>> {
        return combine(
            medicationDao.getAllActiveMedications(),
            doseLogDao.getLogsForDate(date)
        ) { meds, logs ->
            val result = mutableListOf<DoseWithMedication>()
            val logMap = logs.associateBy { "${it.medicationId}_${it.doseTime}" }

            for (med in meds) {
                if (!isScheduledForDate(med, date)) continue
                
                for (time in med.doseTimeList) {
                    val key = "${med.id}_${time}"
                    val log = logMap[key]
                    val status = log?.status ?: "PENDING"
                    result.add(
                        DoseWithMedication(
                            medication = med,
                            scheduledTime = time,
                            scheduledDate = date,
                            doseLog = log,
                            status = status
                        )
                    )
                }
            }
            result.sortedBy { it.scheduledTime }
        }
    }

    suspend fun markDoseTaken(medicationId: Long, date: String, time: String): Medication? {
        val existing = doseLogDao.getLog(medicationId, date, time)
        val wasAlreadyTaken = existing?.status == "TAKEN"

        val updatedLog = DoseLog(
            id = existing?.id ?: 0,
            medicationId = medicationId,
            doseDate = date,
            doseTime = time,
            status = "TAKEN",
            takenTimestamp = System.currentTimeMillis()
        )
        doseLogDao.insertOrUpdate(updatedLog)

        var newlyLowStockMed: Medication? = null

        // Automatically decrement inventory if not previously taken
        if (!wasAlreadyTaken) {
            val medBefore = medicationDao.getMedicationDirect(medicationId)
            medicationDao.decrementStock(medicationId, 1)
            val medAfter = medicationDao.getMedicationDirect(medicationId)
            
            if (medBefore != null && medAfter != null) {
                if (medBefore.currentStock > medBefore.lowStockThreshold && medAfter.currentStock <= medAfter.lowStockThreshold) {
                    newlyLowStockMed = medAfter
                }
            }
        }
        
        return newlyLowStockMed
    }

    suspend fun markDoseSkipped(medicationId: Long, date: String, time: String) {
        val existing = doseLogDao.getLog(medicationId, date, time)
        val wasTaken = existing?.status == "TAKEN"

        val updatedLog = DoseLog(
            id = existing?.id ?: 0,
            medicationId = medicationId,
            doseDate = date,
            doseTime = time,
            status = "SKIPPED",
            takenTimestamp = null
        )
        doseLogDao.insertOrUpdate(updatedLog)

        // If previously marked taken, restore the stock by 1
        if (wasTaken) {
            medicationDao.addStock(medicationId, 1)
        }
    }

    suspend fun resetDoseStatus(medicationId: Long, date: String, time: String) {
        val existing = doseLogDao.getLog(medicationId, date, time)
        if (existing != null) {
            val wasTaken = existing.status == "TAKEN"
            doseLogDao.insertOrUpdate(existing.copy(status = "PENDING", takenTimestamp = null))
            if (wasTaken) {
                medicationDao.addStock(medicationId, 1)
            }
        }
    }
}
