package com.vereshchagin.nikolay.stankinschedule.news.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.vereshchagin.nikolay.stankinschedule.news.model.NewsPost

/**
 * Схема БД используемая для хранения новостей.
 */
@Database(
    entities = [NewsPost::class],
    version = 1,
    exportSchema = false
)
abstract class NewsDb : RoomDatabase() {
    abstract fun news() : NewsDao
}