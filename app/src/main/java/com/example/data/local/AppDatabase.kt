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
    version = 3,
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
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            scope.launch(Dispatchers.IO) {
                                val database = getDatabase(context, scope)
                                database.medicationDao().insertAll(SampleData.getSampleMedications())
                                database.doseLogDao().insertAll(SampleData.getSampleDoseLogs())
                                database.appointmentDao().insertAll(SampleData.getSampleAppointments())
                                database.contactDao().insertAll(SampleData.getSampleContacts())
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
