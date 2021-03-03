package com.vereshchagin.nikolay.stankinschedule.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.vereshchagin.nikolay.stankinschedule.db.dao.NewsDao
import com.vereshchagin.nikolay.stankinschedule.db.dao.ScheduleDao
import com.vereshchagin.nikolay.stankinschedule.model.news.NewsItem
import com.vereshchagin.nikolay.stankinschedule.model.schedule.db.PairItem
import com.vereshchagin.nikolay.stankinschedule.model.schedule.db.ScheduleItem
import com.vereshchagin.nikolay.stankinschedule.utils.convertors.room.DateTimeConverter

/**
 * Главная БД приложения.
 */
@Database(
    entities = [NewsItem::class, ScheduleItem::class, PairItem::class],
    version = 3,
    exportSchema = true
)
@TypeConverters(DateTimeConverter::class)
abstract class MainApplicationDatabase : RoomDatabase() {

    /**
     * Dao новостей приложения.
     */
    abstract fun news(): NewsDao

    /**
     * Dao расписаний приложений.
     */
    abstract fun schedules(): ScheduleDao


    companion object {

        /**
         * Singleton БД.
         */
        @Volatile
        private var instance: MainApplicationDatabase? = null

        /**
         * Возвращает объект БД.
         * @param context контекст для создания БД.
         */
        fun database(context: Context): MainApplicationDatabase {
            val currentInstance = instance
            if (currentInstance != null) {
                return currentInstance
            }

            synchronized(this) {
                val currentInstance2 = instance
                if (currentInstance2 != null) {
                    return currentInstance2
                }

                val database = Room.databaseBuilder(
                    context.applicationContext,
                    MainApplicationDatabase::class.java,
                    "main_application_database"
                ).fallbackToDestructiveMigration()
                    // .allowMainThreadQueries()
                    .build()

//                runBlocking {
//                    database.withTransaction {
//                        ScheduleRepository.migrateSchedules(context, database)
//                    }
//                }

                instance = database
                return database
            }
        }
    }
}