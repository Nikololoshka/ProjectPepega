package com.vereshchagin.nikolay.stankinschedule.schedule.core.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        ScheduleEntity::class,
        PairEntity::class,
    ],
    version = 1,
    exportSchema = true
)
abstract class ScheduleDatabase : RoomDatabase() {

    abstract fun schedule(): ScheduleDao

    companion object {
        /**
         * Singleton БД.
         */
        @Volatile
        private var instance: ScheduleDatabase? = null

        fun database(context: Context): ScheduleDatabase {
            val currentInstance = instance
            if (currentInstance != null) {
                return currentInstance
            }

            synchronized(this) {
                val currentInstance2 = instance
                if (currentInstance2 != null) {
                    return currentInstance2
                }

                val databaseBuilder = Room.databaseBuilder(
                    context,
                    ScheduleDatabase::class.java,
                    "schedule_database"
                ).fallbackToDestructiveMigration()

                val database = databaseBuilder.build()
                instance = database
                return database
            }
        }
    }
}