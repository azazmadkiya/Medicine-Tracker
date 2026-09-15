package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.DoseLog
import kotlinx.coroutines.flow.Flow

@Dao
interface DoseLogDao {
    @Query("SELECT * FROM dose_logs WHERE doseDate = :date ORDER BY doseTime ASC")
    fun getLogsForDate(date: String): Flow<List<DoseLog>>

    @Query("SELECT * FROM dose_logs WHERE medicationId = :medicationId AND doseDate = :date AND doseTime = :time LIMIT 1")
    suspend fun getLog(medicationId: Long, date: String, time: String): DoseLog?

    @Query("SELECT * FROM dose_logs WHERE doseDate >= :startDate AND doseDate <= :endDate")
    fun getLogsForDateRange(startDate: String, endDate: String): Flow<List<DoseLog>>

    @Query("SELECT COUNT(*) FROM dose_logs WHERE doseDate = :date AND status = 'TAKEN'")
    fun getTakenCountForDate(date: String): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(doseLog: DoseLog): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(logs: List<DoseLog>)

    @Update
    suspend fun updateLog(doseLog: DoseLog)

    @Query("DELETE FROM dose_logs WHERE medicationId = :medicationId")
    suspend fun deleteLogsForMedication(medicationId: Long)
}
