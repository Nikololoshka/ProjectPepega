package com.vereshchagin.nikolay.stankinschedule.db

import android.content.Context
import androidx.room.*
import com.vereshchagin.nikolay.stankinschedule.db.dao.NewsDao
import com.vereshchagin.nikolay.stankinschedule.db.dao.RepositoryDao
import com.vereshchagin.nikolay.stankinschedule.db.dao.ScheduleDao
import com.vereshchagin.nikolay.stankinschedule.model.news.NewsItem
import com.vereshchagin.nikolay.stankinschedule.model.schedule.db.PairItem
import com.vereshchagin.nikolay.stankinschedule.model.schedule.db.ScheduleItem
import com.vereshchagin.nikolay.stankinschedule.model.schedule.repository.v1.CategoryEntry
import com.vereshchagin.nikolay.stankinschedule.model.schedule.repository.v1.ScheduleEntry
import com.vereshchagin.nikolay.stankinschedule.repository.ScheduleRepository
import com.vereshchagin.nikolay.stankinschedule.settings.SchedulePreference
import com.vereshchagin.nikolay.stankinschedule.utils.convertors.room.DateTimeConverter
import com.vereshchagin.nikolay.stankinschedule.utils.convertors.room.ListConverter
import kotlinx.coroutines.runBlocking

/**
 * Главная БД приложения.
 */
@Database(
    entities = [
        NewsItem::class,
        ScheduleItem::class,
        PairItem::class,
        CategoryEntry::class,
        ScheduleEntry::class
    ],
    version = 3,
    exportSchema = true
)
@TypeConverters(DateTimeConverter::class, ListConverter::class)
abstract class MainApplicationDatabase : RoomDatabase() {

    /**
     * Dao репозитория приложения.
     */
    abstract fun repository(): RepositoryDao

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

                val databaseBuilder = Room.databaseBuilder(
                    context.applicationContext,
                    MainApplicationDatabase::class.java,
                    "main_application_database"
                ).fallbackToDestructiveMigration()

                val migrate = SchedulePreference.migrateSchedule(context)
                val database = if (migrate) {
                    databaseBuilder.allowMainThreadQueries().build()
                } else {
                    databaseBuilder.build()
                }

                if (!migrate) {
                    runBlocking {
                        database.withTransaction {
                            ScheduleRepository.migrateSchedules(context, database)
                        }
                    }
                    SchedulePreference.setMigrateSchedule(context, true)
                }

                instance = database
                return database
            }
        }
    }
}