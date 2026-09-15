package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.SampleData
import com.example.data.local.entity.Medication
import com.example.data.repository.DoseWithMedication
import com.example.data.repository.MedicationRepository
import com.example.notifications.AlarmScheduler
import com.example.notifications.NotificationHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class DailyAdherence(
    val totalScheduled: Int = 0,
    val takenCount: Int = 0,
    val skippedCount: Int = 0,
    val pendingCount: Int = 0,
    val adherenceRate: Float = 0f
)

data class DayHistory(
    val dateString: String,
    val dayOfWeek: String, // Mon, Tue...
    val isScheduled: Boolean,
    val isTaken: Boolean,
    val isToday: Boolean
)

data class MedicationHistory(
    val medication: Medication,
    val historyDays: List<DayHistory>
)

class MedicineViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MedicationRepository
    private val alarmScheduler = AlarmScheduler(application)
    private val notificationHelper = NotificationHelper(application)

    private val _selectedDate = MutableStateFlow(SampleData.getTodayString())
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        val db = AppDatabase.getDatabase(application)
        repository = MedicationRepository(db.medicationDao(), db.doseLogDao())
    }

    val allMedications: StateFlow<List<Medication>> = repository.allActiveMedications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val lowStockMedications: StateFlow<List<Medication>> = repository.lowStockMedications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val currentDoses: StateFlow<List<DoseWithMedication>> = _selectedDate
        .flatMapLatest { date -> repository.getDosesForDate(date) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val adherence: StateFlow<DailyAdherence> = currentDoses.combine(_selectedDate) { doses, _ ->
        val total = doses.size
        val taken = doses.count { it.status == "TAKEN" }
        val skipped = doses.count { it.status == "SKIPPED" }
        val pending = doses.count { it.status == "PENDING" }
        val rate = if (total > 0) (taken.toFloat() / total.toFloat()) else 0f
        DailyAdherence(
            totalScheduled = total,
            takenCount = taken,
            skippedCount = skipped,
            pendingCount = pending,
            adherenceRate = rate
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DailyAdherence())

    val medicationHistoryList: StateFlow<List<MedicationHistory>> = repository.allActiveMedications.combine(
        repository.getLogsForDateRange(
            startDate = {
                val cal = Calendar.getInstance()
                cal.add(Calendar.DAY_OF_YEAR, -6)
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time)
            }(),
            endDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        )
    ) { meds, logs ->
        val result = mutableListOf<MedicationHistory>()
        
        val dates = mutableListOf<String>()
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -6) // 7 days including today
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dowFormat = SimpleDateFormat("EEE", Locale.getDefault())
        val todayStr = format.format(Date())
        
        for (i in 0..6) {
            dates.add(format.format(cal.time))
            cal.add(Calendar.DAY_OF_YEAR, 1)
        }
        
        for (med in meds) {
            val historyDays = mutableListOf<DayHistory>()
            for (date in dates) {
                val isScheduled = repository.isScheduledForDate(med, date)
                val isTaken = logs.any { it.medicationId == med.id && it.doseDate == date && it.status == "TAKEN" }
                
                val parsedDate = format.parse(date)
                val dow = if (parsedDate != null) dowFormat.format(parsedDate) else ""
                
                historyDays.add(DayHistory(
                    dateString = date,
                    dayOfWeek = dow,
                    isScheduled = isScheduled,
                    isTaken = isTaken,
                    isToday = date == todayStr
                ))
            }
            result.add(MedicationHistory(med, historyDays))
        }
        result
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setSelectedDate(date: String) {
        _selectedDate.value = date
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun markDoseTaken(dose: DoseWithMedication) {
        viewModelScope.launch {
            val newlyLowStockMed = repository.markDoseTaken(dose.medication.id, dose.scheduledDate, dose.scheduledTime)
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

    fun markDoseSkipped(dose: DoseWithMedication) {
        viewModelScope.launch {
            repository.markDoseSkipped(dose.medication.id, dose.scheduledDate, dose.scheduledTime)
        }
    }

    fun resetDoseStatus(dose: DoseWithMedication) {
        viewModelScope.launch {
            repository.resetDoseStatus(dose.medication.id, dose.scheduledDate, dose.scheduledTime)
        }
    }

    fun addMedication(medication: Medication) {
        viewModelScope.launch {
            val newId = repository.addMedication(medication)
            val medWithId = medication.copy(id = newId)
            for (time in medWithId.doseTimeList) {
                alarmScheduler.scheduleMedicationDose(medWithId, time)
            }
        }
    }

    fun updateMedication(medication: Medication) {
        viewModelScope.launch {
            repository.updateMedication(medication)
            for (time in medication.doseTimeList) {
                alarmScheduler.scheduleMedicationDose(medication, time)
            }
        }
    }

    fun deleteMedication(medication: Medication) {
        viewModelScope.launch {
            for (time in medication.doseTimeList) {
                alarmScheduler.cancelMedicationAlarm(medication, time)
            }
            repository.deleteMedication(medication)
        }
    }

    fun refillStock(medicationId: Long, amount: Int) {
        viewModelScope.launch {
            repository.addStock(medicationId, amount)
        }
    }

    fun triggerTestNotification(medication: Medication) {
        notificationHelper.showMedicationNotification(
            medicationId = medication.id,
            medName = medication.name,
            dosage = medication.dosage,
            instruction = medication.instruction,
            doseTime = medication.doseTimeList.firstOrNull() ?: "Now"
        )
    }

    fun triggerLowStockTestNotification(medication: Medication) {
        notificationHelper.showLowStockNotification(
            medicationId = medication.id,
            medName = medication.name,
            stockLeft = medication.currentStock,
            unit = medication.unitType
        )
    }
}
