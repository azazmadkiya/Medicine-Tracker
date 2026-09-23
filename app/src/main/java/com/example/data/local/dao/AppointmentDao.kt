package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.Appointment
import kotlinx.coroutines.flow.Flow

@Dao
interface AppointmentDao {
    @Query("SELECT * FROM appointments")
    suspend fun getAllAppointmentsSync(): List<Appointment>

    @Query("SELECT * FROM appointments ORDER BY dateTimeMillis ASC")
    fun getAllAppointments(): Flow<List<Appointment>>

    @Query("SELECT * FROM appointments WHERE status = 'UPCOMING' AND dateTimeMillis >= :nowMillis ORDER BY dateTimeMillis ASC")
    fun getUpcomingAppointments(nowMillis: Long = System.currentTimeMillis()): Flow<List<Appointment>>

    @Query("SELECT * FROM appointments WHERE status = 'COMPLETED' OR (status = 'UPCOMING' AND dateTimeMillis < :nowMillis) ORDER BY dateTimeMillis DESC")
    fun getPastAppointments(nowMillis: Long = System.currentTimeMillis()): Flow<List<Appointment>>

    @Query("SELECT * FROM appointments WHERE id = :id")
    fun getAppointmentById(id: Long): Flow<Appointment?>

    @Query("SELECT COUNT(*) FROM appointments")
    suspend fun getAppointmentCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppointment(appointment: Appointment): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(appointments: List<Appointment>)

    @Update
    suspend fun updateAppointment(appointment: Appointment)

    @Query("UPDATE appointments SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Long, status: String)

    @Delete
    suspend fun deleteAppointment(appointment: Appointment)
}
 
