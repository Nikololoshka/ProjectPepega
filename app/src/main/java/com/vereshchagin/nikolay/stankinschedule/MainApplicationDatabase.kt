package com.vereshchagin.nikolay.stankinschedule

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.vereshchagin.nikolay.stankinschedule.news.review.categories.repository.db.NewsDao
import com.vereshchagin.nikolay.stankinschedule.news.review.categories.repository.model.NewsItem

/**
 * Главная БД приложения.
 */
@Database(
    entities = [NewsItem::class],
    version = 1,
    exportSchema = false
)
abstract class MainApplicationDatabase : RoomDatabase() {

    /**
     * Dao новостей приложения.
     */
    abstract fun news() : NewsDao

    companion object {

        /**
         * Singleton БД.
         */
        @Volatile
        private var INSTANCE: MainApplicationDatabase? = null

        /**
         * Возвращает объект БД.
         * @param context контекст для создания БД.
         */
        fun database(context: Context): MainApplicationDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }

            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MainApplicationDatabase::class.java,
                    "main_application_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}