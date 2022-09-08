package com.vereshchagin.nikolay.stankinschedule.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.vereshchagin.nikolay.stankinschedule.news.core.data.db.NewsDao
import com.vereshchagin.nikolay.stankinschedule.news.core.data.db.NewsDatabaseDao
import com.vereshchagin.nikolay.stankinschedule.news.core.data.db.NewsEntity
import com.vereshchagin.nikolay.stankinschedule.schedule.core.data.db.PairEntity
import com.vereshchagin.nikolay.stankinschedule.schedule.core.data.db.ScheduleDao
import com.vereshchagin.nikolay.stankinschedule.schedule.core.data.db.ScheduleDatabaseDao
import com.vereshchagin.nikolay.stankinschedule.schedule.core.data.db.ScheduleEntity
import com.vereshchagin.nikolay.stankinschedule.schedule.repository.data.db.RepositoryDao
import com.vereshchagin.nikolay.stankinschedule.schedule.repository.data.db.RepositoryDatabaseDao
import com.vereshchagin.nikolay.stankinschedule.schedule.repository.data.db.RepositoryEntity

/**
 * Главная БД приложения.
 */
@Database(
    entities = [
        NewsEntity::class,

        ScheduleEntity::class,
        PairEntity::class,

        RepositoryEntity::class
    ],
    version = 3,
    exportSchema = false
)
// @TypeConverters(DateTimeConverter::class, ListConverter::class)
abstract class MainApplicationDatabase : RoomDatabase(), NewsDatabaseDao, RepositoryDatabaseDao,
    ScheduleDatabaseDao {

    /**
     * Dao новостей приложения.
     */
    abstract override fun news(): NewsDao

    /**
     * Dao расписаний приложений.
     */
    abstract override fun schedule(): ScheduleDao

    /**
     * Dao репозитория приложения.
     */
    abstract override fun repository(): RepositoryDao

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

                /*
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
                */

                val database = databaseBuilder.build()
                instance = database
                return database
            }
        }
    }
}