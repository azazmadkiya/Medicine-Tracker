package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.dao.AppointmentDao
import com.example.data.local.dao.ContactDao
import com.example.data.local.dao.DoseLogDao
import com.example.data.local.dao.MedicationDao
import com.example.data.local.entity.Appointment
import com.example.data.local.entity.Contact
import com.example.data.local.entity.DoseLog
import com.example.data.local.entity.Medication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        Medication::class,
        DoseLog::class,
        Appointment::class,
        Contact::class
    ],
    version = 5,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun medicationDao(): MedicationDao
    abstract fun doseLogDao(): DoseLogDao
    abstract fun appointmentDao(): AppointmentDao
    abstract fun contactDao(): ContactDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope = CoroutineScope(Dispatchers.IO)): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "medicine_tracker_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
