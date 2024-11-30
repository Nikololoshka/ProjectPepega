package com.vereshchagin.nikolay.stankinschedule.news.core.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        NewsEntity::class,
    ],
    version = 2,
    exportSchema = true
)
abstract class NewsDatabase : RoomDatabase() {

    abstract fun news(): NewsDao

    companion object {
        /**
         * Singleton БД.
         */
        @Volatile
        private var instance: NewsDatabase? = null

        fun database(context: Context): NewsDatabase {
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
                    NewsDatabase::class.java,
                    "news_database"
                ).fallbackToDestructiveMigration()

                val database = databaseBuilder.build()
                instance = database
                return database
            }
        }
    }
}