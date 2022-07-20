package com.vereshchagin.nikolay.stankinschedule.db

import android.content.Context
import androidx.room.*
import com.vereshchagin.nikolay.stankinschedule.db.dao.NewsDao
import com.vereshchagin.nikolay.stankinschedule.db.dao.RepositoryDao
import com.vereshchagin.nikolay.stankinschedule.db.dao.ScheduleDao
import com.vereshchagin.nikolay.stankinschedule.model.news.NewsItem
import com.vereshchagin.nikolay.stankinschedule.model.schedule.db.PairItem
import com.vereshchagin.nikolay.stankinschedule.model.schedule.db.ScheduleItem
import com.vereshchagin.nikolay.stankinschedule.model.schedule.remote.ScheduleCategoryEntry
import com.vereshchagin.nikolay.stankinschedule.model.schedule.remote.ScheduleItemEntry
import com.vereshchagin.nikolay.stankinschedule.model.schedule.remote.ScheduleUpdateEntry
import com.vereshchagin.nikolay.stankinschedule.news.data.db.NewsDatabaseDao
import com.vereshchagin.nikolay.stankinschedule.news.data.db.NewsEntity
import com.vereshchagin.nikolay.stankinschedule.repository.ScheduleRepository
import com.vereshchagin.nikolay.stankinschedule.settings.SchedulePreferenceKt
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

        ScheduleCategoryEntry::class,
        ScheduleItemEntry::class,
        ScheduleUpdateEntry::class,

        NewsEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(DateTimeConverter::class, ListConverter::class)
abstract class MainApplicationDatabase : RoomDatabase(), NewsDatabaseDao {

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

    abstract override fun featureNews(): com.vereshchagin.nikolay.stankinschedule.news.data.db.NewsDao

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
                    context,
                    MainApplicationDatabase::class.java,
                    "main_application_database"
                ).fallbackToDestructiveMigration()

                val preference = SchedulePreferenceKt(context)
                val database = if (preference.migrateToVersion2) {
                    databaseBuilder.allowMainThreadQueries().build()
                } else {
                    databaseBuilder.build()
                }

                if (!preference.migrateToVersion2) {
                    runBlocking {
                        database.withTransaction {
                            ScheduleRepository.migrateSchedules(context, database)
                        }
                    }
                    preference.migrateToVersion2 = true
                }

                instance = database
                return database
            }
        }
    }
}