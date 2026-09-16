package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medications")
data class Medication(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val dosage: String,
    val form: String = "Tablet", // Tablet, Capsule, Liquid, Drops, Inhaler, Injection
    val instruction: String = "After meal", // Before meal, With meal, After meal, Empty stomach
    val frequency: String = "Daily", // Display string (e.g., "Daily", "Mon, Wed, Fri", "On Demand")
    val frequencyType: String = "Daily", // "Daily", "OnDemand", "SpecificDays", "Interval", "Cyclic"
    val frequencyData: String = "", // Holds specific days ("Mon,Wed") or interval ("2") or cyclic ("21,7")
    val startDate: String = "", // YYYY-MM-DD for calculating intervals
    val doseTimes: String = "08:00", // Comma-separated "08:00,20:00"
    val currentStock: Int = 30,
    val totalPackSize: Int = 30,
    val lowStockThreshold: Int = 7,
    val unitType: String = "pills", // pills, capsules, ml, sprays, drops
    val colorHex: String = "#006D77",
    val expiryDate: String = "",
    val doctorOrRx: String = "",
    val notes: String = "",
    val isActive: Boolean = true
) {
    val doseTimeList: List<String>
        get() = if (doseTimes.isBlank()) emptyList() else doseTimes.split(",").map { it.trim() }

    val isLowStock: Boolean
        get() = currentStock <= lowStockThreshold

    val stockPercentage: Float
        get() = if (totalPackSize > 0) (currentStock.toFloat() / totalPackSize.toFloat()).coerceIn(0f, 1f) else 0f

    val dailyDoseCount: Int
        get() = doseTimeList.size.coerceAtLeast(1)

    val estimatedDaysLeft: Int
        get() {
            if (dailyDoseCount == 0) return currentStock
            if (currentStock == 0) return 0
            
            return when (frequencyType) {
                "OnDemand" -> currentStock // Can't easily estimate, fallback to 1/day
                "Interval" -> {
                    val interval = frequencyData.toIntOrNull() ?: 1
                    (currentStock * interval) / dailyDoseCount
                }
                "SpecificDays" -> {
                    val daysAWeek = frequencyData.split(",").filter { it.isNotBlank() }.size
                    val safeDaysAWeek = if (daysAWeek > 0) daysAWeek else 7
                    (currentStock * 7) / (dailyDoseCount * safeDaysAWeek)
                }
                "Cyclic" -> {
                    val parts = frequencyData.split(",")
                    val intakeDays = parts.getOrNull(0)?.toIntOrNull() ?: 21
                    val pauseDays = parts.getOrNull(1)?.toIntOrNull() ?: 7
                    val cycleLength = intakeDays + pauseDays
                    (currentStock * cycleLength) / (dailyDoseCount * intakeDays)
                }
                else -> currentStock / dailyDoseCount // Daily
            }
        }
        
    val estimatedTimeLeftFormatted: String
        get() {
            val days = estimatedDaysLeft
            if (days == 0) return "0 days left"
            if (days < 30) return "~$days ${if (days == 1) "day" else "days"} left"
            
            val months = days / 30
            val remainingDays = days % 30
            
            return buildString {
                append("~")
                append(months)
                append(if (months == 1) " month" else " months")
                if (remainingDays > 0) {
                    append(", ")
                    append(remainingDays)
                    append(if (remainingDays == 1) " day" else " days")
                }
                append(" left")
            }
        }
}
