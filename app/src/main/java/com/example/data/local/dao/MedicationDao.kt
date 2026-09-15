package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.Medication
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicationDao {
    @Query("SELECT * FROM medications WHERE isActive = 1 ORDER BY name ASC")
    fun getAllActiveMedications(): Flow<List<Medication>>

    @Query("SELECT * FROM medications ORDER BY name ASC")
    fun getAllMedications(): Flow<List<Medication>>

    @Query("SELECT * FROM medications WHERE id = :id")
    fun getMedicationById(id: Long): Flow<Medication?>

    @Query("SELECT * FROM medications WHERE id = :id")
    suspend fun getMedicationDirect(id: Long): Medication?

    @Query("SELECT * FROM medications WHERE isActive = 1 AND currentStock <= lowStockThreshold ORDER BY currentStock ASC")
    fun getLowStockMedications(): Flow<List<Medication>>

    @Query("SELECT COUNT(*) FROM medications")
    suspend fun getMedicationCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedication(medication: Medication): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(medications: List<Medication>)

    @Update
    suspend fun updateMedication(medication: Medication)

    @Query("UPDATE medications SET currentStock = CASE WHEN currentStock - :amount < 0 THEN 0 ELSE currentStock - :amount END WHERE id = :id")
    suspend fun decrementStock(id: Long, amount: Int = 1)

    @Query("UPDATE medications SET currentStock = currentStock + :amount WHERE id = :id")
    suspend fun addStock(id: Long, amount: Int)

    @Query("UPDATE medications SET currentStock = :newStock WHERE id = :id")
    suspend fun setStock(id: Long, newStock: Int)

    @Delete
    suspend fun deleteMedication(medication: Medication)
}
