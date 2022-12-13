package com.vereshchagin.nikolay.stankinschedule.schedule.repository.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * TODO("Fix бд")
 * warning: Schema export directory is not provided to the annotation processor so
 * we cannot export the schema. You can either provide `room.schemaLocation`
 * annotation processor argument OR set exportSchema to false.
 */
@Database(
    entities = [
        RepositoryEntity::class,
    ],
    version = 1,
    exportSchema = true
)

abstract class RepositoryDatabase : RoomDatabase() {

    abstract fun repository(): RepositoryDao

    companion object {
        /**
         * Singleton БД.
         */
        @Volatile
        private var instance: RepositoryDatabase? = null

        fun database(context: Context): RepositoryDatabase {
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
                    RepositoryDatabase::class.java,
                    "schedule_repository_database"
                ).fallbackToDestructiveMigration()

                val database = databaseBuilder.build()
                instance = database
                return database
            }
        }
    }
}