package com.vereshchagin.nikolay.stankinschedule.repository

import android.util.Log
import androidx.paging.PagingSource
import androidx.room.withTransaction
import com.vereshchagin.nikolay.stankinschedule.api.StankinNewsAPI
import com.vereshchagin.nikolay.stankinschedule.db.MainApplicationDatabase
import com.vereshchagin.nikolay.stankinschedule.db.dao.NewsDao
import com.vereshchagin.nikolay.stankinschedule.model.news.NewsItem
import com.vereshchagin.nikolay.stankinschedule.model.news.NewsResponse
import com.vereshchagin.nikolay.stankinschedule.settings.NewsPreference
import com.vereshchagin.nikolay.stankinschedule.utils.DateTimeUtils
import org.joda.time.DateTime
import retrofit2.await
import javax.inject.Inject

/**
 * Репозиторий с новостями.
 */
class NewsRepository @Inject constructor(
    private val newsAPI: StankinNewsAPI,
    private val db: MainApplicationDatabase,
    private val newsDao: NewsDao,
    private val preference: NewsPreference,
) {

    /**
     * Добавляет новостные посты из ответа от сервера в БД.
     *
     * @param newsSubdivision номер подразделения.
     * @param response ответ с новостями от сервера.
     * @param fullRefresh удалять ли ранее сохраненные новости.
     */
    suspend fun addPostsIntoDb(
        newsSubdivision: Int,
        response: NewsResponse,
        fullRefresh: Boolean = false,
    ) {
        val items = response.data.news

        if (items.isNotEmpty()) {
            db.withTransaction {
                // если обновляем список
                if (fullRefresh) {
                    newsDao.clear(newsSubdivision)
                }

                // индекс по порядку
                val start = newsDao.nextIndexInResponse(newsSubdivision)

                // добавление индекса и номера отдела
                val news = items.mapIndexed { index, newsPost ->
                    newsPost.indexInResponse = start + index
                    newsPost.newsSubdivision = newsSubdivision
                    newsPost
                }

                newsDao.insert(news)
                preference.setNewsUpdate(newsSubdivision)
            }
        }
    }

    /**
     * Возвращает список новостей подразделения по номеру страницы
     * и количеству необходимых новостей.
     *
     * @param newsSubdivision номер подразделения.
     * @param page номер страницы.
     * @param count количество новостей.
     */
    suspend fun news(newsSubdivision: Int, page: Int = 1, count: Int = 40): NewsResponse {
        return StankinNewsAPI.getNews(newsAPI, newsSubdivision, page, count).await()
    }

    /**
     * Возвращает источник данных новостей (из БД).
     *
     * @param newsSubdivision номер подразделения.
     */
    fun pagingSource(newsSubdivision: Int): PagingSource<Int, NewsItem> {
        return db.news().all(newsSubdivision)
    }

    /**
     * Обновляет новости для подразделения.
     *
     * @param newsSubdivision номер подразделения.
     */
    suspend fun refresh(newsSubdivision: Int) {
        try {
            val lastTime = preference.lastNewsUpdate(newsSubdivision)
            Log.d("NewsRepositoryLog", "refresh: $newsSubdivision - $lastTime")
            if (lastTime != null && DateTimeUtils.between(lastTime, DateTime.now()) < 30) {
                return
            }

            val response = news(newsSubdivision)
            addPostsIntoDb(newsSubdivision, response, true)

        } catch (ignored: Exception) {

        }
    }

    /**
     * Обновляет все новости приложения.
     */
    suspend fun updateAll() {
        SUBDIVISIONS.forEach { newsSubdivision ->
            refresh(newsSubdivision)
        }
    }

    /**
     * Возвращает LiveData со списком последних новостей.
     *
     * @param count количество последних новостей в списке.
     */
    fun latest(count: Int = 3) = newsDao.latest(count)

    companion object {
        /**
         * Подразделения новостей (для репозиториев).
         */
        private val SUBDIVISIONS = listOf(0, 125)
    }

}