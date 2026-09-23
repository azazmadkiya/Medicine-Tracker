package com.example.utils

import android.content.Context
import android.net.Uri
import com.example.data.local.AppDatabase
import com.example.data.local.BackupData
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object BackupManager {
    suspend fun exportData(context: Context, uri: Uri): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val db = AppDatabase.getDatabase(context)
            val medications = db.medicationDao().getAllMedicationsSync()
            val doseLogs = db.doseLogDao().getAllLogsSync()
            val appointments = db.appointmentDao().getAllAppointmentsSync()
            val contacts = db.contactDao().getAllContactsSync()

            val backupData = BackupData(medications, doseLogs, appointments, contacts)
            val gson = GsonBuilder().setPrettyPrinting().create()
            val jsonString = gson.toJson(backupData)

            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                outputStream.write(jsonString.toByteArray())
            } ?: return@withContext Result.failure(Exception("Cannot open file for writing"))

            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun importData(context: Context, uri: Uri): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val jsonString = context.contentResolver.openInputStream(uri)?.use { inputStream ->
                inputStream.bufferedReader().use { it.readText() }
            } ?: return@withContext Result.failure(Exception("Cannot open file for reading"))

            val gson = Gson()
            val backupData = gson.fromJson(jsonString, BackupData::class.java)

            val db = AppDatabase.getDatabase(context)
            db.clearAllTables() // Wipe existing data so we don't duplicate or conflict unexpectedly

            if (backupData.medications.isNotEmpty()) {
                db.medicationDao().insertAll(backupData.medications)
            }
            if (backupData.doseLogs.isNotEmpty()) {
                db.doseLogDao().insertAll(backupData.doseLogs)
            }
            if (backupData.appointments.isNotEmpty()) {
                db.appointmentDao().insertAll(backupData.appointments)
            }
            if (backupData.contacts.isNotEmpty()) {
                db.contactDao().insertAll(backupData.contacts)
            }

            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}
