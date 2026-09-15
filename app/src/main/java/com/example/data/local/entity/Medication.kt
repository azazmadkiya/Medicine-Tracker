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
        get() = if (dailyDoseCount > 0) currentStock / dailyDoseCount else currentStock
}
