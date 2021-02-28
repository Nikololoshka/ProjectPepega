package com.vereshchagin.nikolay.stankinschedule.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
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
                )//.addMigrations(MIGRATION_1_2)
                    .fallbackToDestructiveMigration()
                    // .allowMainThreadQueries()
                    .build()

//                runBlocking {
//                    database.withTransaction {
//                        val repository = ScheduleRepository()
//                        for (scheduleName in repository.schedules(context)) {
//                            ScheduleRepositoryKt.saveResponse(
//                                repository.path(context, scheduleName),
//                                scheduleName,
//                                database
//                            )
//                        }
//                    }
//                }

                instance = database
                return database
            }
        }

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                        CREATE TABLE schedules (
                            schedule_name TEXT NOT NULL PRIMARY KEY,
                            created_date TEXT NOT NULL,
                            edit_date TEXT NOT NULL,
                            synchronized INTEGER NOT NULL,
                            position INTEGER NOT NULL,
                            favorite INTEGER NOT NULL
                        )
                    """
                )
                database.execSQL(
                    """
                        CREATE TABLE pairs (
                            id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
                            schedule_name TEXT NOT NULL,
                            title TEXT NOT NULL,
                            lecturer TEXT NOT NULL,
                            classroom TEXT NOT NULL,
                            type TEXT NOT NULL,
                            subgroup TEXT NOT NULL,
                            time TEXT NOT NULL,
                            date TEXT NOT NULL,

                            FOREIGN KEY (`schedule_name`) REFERENCES schedules(schedule_name) ON UPDATE NO ACTION ON DELETE CASCADE
                        )
                    """
                )

                database.execSQL(
                    """
                        CREATE INDEX index_pairs_schedule_name ON pairs(schedule_name)
                    """
                )
            }
        }
    }
}