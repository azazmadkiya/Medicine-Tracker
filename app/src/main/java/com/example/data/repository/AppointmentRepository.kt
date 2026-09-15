package com.example.data.repository

import com.example.data.local.dao.AppointmentDao
import com.example.data.local.entity.Appointment
import kotlinx.coroutines.flow.Flow

class AppointmentRepository(
    private val appointmentDao: AppointmentDao
) {
    val upcomingAppointments: Flow<List<Appointment>> = appointmentDao.getUpcomingAppointments()
    val pastAppointments: Flow<List<Appointment>> = appointmentDao.getPastAppointments()
    val allAppointments: Flow<List<Appointment>> = appointmentDao.getAllAppointments()

    fun getAppointmentById(id: Long): Flow<Appointment?> = appointmentDao.getAppointmentById(id)

    suspend fun scheduleAppointment(appointment: Appointment): Long = appointmentDao.insertAppointment(appointment)

    suspend fun updateAppointment(appointment: Appointment) = appointmentDao.updateAppointment(appointment)

    suspend fun markCompleted(id: Long) = appointmentDao.updateStatus(id, "COMPLETED")

    suspend fun cancelAppointment(id: Long) = appointmentDao.updateStatus(id, "CANCELLED")

    suspend fun deleteAppointment(appointment: Appointment) = appointmentDao.deleteAppointment(appointment)
}
