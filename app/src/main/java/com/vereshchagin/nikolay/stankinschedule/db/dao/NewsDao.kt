package com.vereshchagin.nikolay.stankinschedule.db.dao

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.vereshchagin.nikolay.stankinschedule.model.news.NewsItem

/**
 * Интерфейс для работы с БД новостей.
 */
@Dao
interface NewsDao {

    /**
     * Вставка (обновление) новостей в БД.
     * @param items список новостей.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(items: List<NewsItem>)

    /**
     * Возвращает список (DataSource) за кэшированных новостей.
     * @param newsSubdivision номер отдела новостей.
     */
    @Query("SELECT * FROM posts WHERE newsSubdivision = :newsSubdivision ORDER BY indexInResponse ASC")
    fun all(newsSubdivision: Int): PagingSource<Int, NewsItem>

    /**
     * Возвращает список (DataSource) из последних нескольких элементов.
     * @param max максимальное количество элементов.
     */
    @Query("SELECT * FROM POSTS ORDER BY date DESC, id DESC LIMIT :max")
    fun latest(max: Int = 3) : LiveData<List<NewsItem>>

    /**
     * Очищает за кэшированные новости.
     * @param newsSubdivision номер отдела новостей.
     */
    @Query("DELETE FROM posts WHERE newsSubdivision = :newsSubdivision")
    fun clear(newsSubdivision: Int)

    /**
     * Количество новостей в кэше.
     * @param newsSubdivision номер отдела новостей.
     */
    @Query("SELECT COUNT(*) FROM posts WHERE newsSubdivision = :newsSubdivision")
    fun count(newsSubdivision: Int) : Int

    /**
     * Возвращает следующий порядковый индекс для новостей.
     * @param newsSubdivision номер отдела новостей.
     */
    @Query("SELECT MAX(indexInResponse) + 1 FROM posts WHERE newsSubdivision = :newsSubdivision")
    fun nextIndexInResponse(newsSubdivision: Int) : Int
}