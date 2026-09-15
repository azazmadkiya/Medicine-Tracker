package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.entity.Appointment
import com.example.data.repository.AppointmentRepository
import com.example.notifications.AlarmScheduler
import com.example.notifications.NotificationHelper
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AppointmentViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AppointmentRepository
    private val alarmScheduler = AlarmScheduler(application)
    private val notificationHelper = NotificationHelper(application)

    init {
        val db = AppDatabase.getDatabase(application)
        repository = AppointmentRepository(db.appointmentDao())
    }

    val upcomingAppointments: StateFlow<List<Appointment>> = repository.upcomingAppointments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pastAppointments: StateFlow<List<Appointment>> = repository.pastAppointments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allAppointments: StateFlow<List<Appointment>> = repository.allAppointments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun scheduleAppointment(appointment: Appointment) {
        viewModelScope.launch {
            val id = repository.scheduleAppointment(appointment)
            val scheduled = appointment.copy(id = id)
            alarmScheduler.scheduleAppointmentReminder(scheduled)
        }
    }

    fun updateAppointment(appointment: Appointment) {
        viewModelScope.launch {
            repository.updateAppointment(appointment)
            alarmScheduler.scheduleAppointmentReminder(appointment)
        }
    }

    fun markCompleted(id: Long) {
        viewModelScope.launch {
            repository.markCompleted(id)
            alarmScheduler.cancelAppointmentReminder(id)
        }
    }

    fun cancelAppointment(id: Long) {
        viewModelScope.launch {
            repository.cancelAppointment(id)
            alarmScheduler.cancelAppointmentReminder(id)
        }
    }

    fun deleteAppointment(appointment: Appointment) {
        viewModelScope.launch {
            repository.deleteAppointment(appointment)
            alarmScheduler.cancelAppointmentReminder(appointment.id)
        }
    }

    fun triggerTestNotification(appointment: Appointment) {
        notificationHelper.showAppointmentNotification(
            appointmentId = appointment.id,
            doctorName = appointment.doctorName,
            specialty = appointment.specialty,
            clinicName = appointment.clinicName,
            timeFormatted = "${appointment.formattedDate} at ${appointment.formattedTime}"
        )
    }
}
