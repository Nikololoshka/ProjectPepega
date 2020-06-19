package com.vereshchagin.nikolay.stankinschedule.news.db

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.vereshchagin.nikolay.stankinschedule.news.model.NewsPost

/**
 * Интерфейс для работы с БД новостей.
 */
@Dao
interface NewsDao {

    /**
     * Вставка (обновление) новостей в БД.
     * @param posts список новостей.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(posts: List<NewsPost>)

    @Query("SELECT * FROM posts ORDER BY id DESC")
    fun all() : DataSource.Factory<Int, NewsPost>

    @Query("DELETE FROM posts")
    fun clear()
}