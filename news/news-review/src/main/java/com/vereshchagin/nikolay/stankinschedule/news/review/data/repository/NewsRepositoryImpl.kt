package com.vereshchagin.nikolay.stankinschedule.news.review.data.repository

import androidx.paging.PagingSource
import androidx.room.RoomDatabase
import androidx.room.withTransaction
import com.vereshchagin.nikolay.stankinschedule.core.ui.PreferenceManager
import com.vereshchagin.nikolay.stankinschedule.core.ui.subMinutes
import com.vereshchagin.nikolay.stankinschedule.news.core.data.api.NewsResponse
import com.vereshchagin.nikolay.stankinschedule.news.core.data.api.StankinNewsAPI
import com.vereshchagin.nikolay.stankinschedule.news.core.data.db.NewsDao
import com.vereshchagin.nikolay.stankinschedule.news.core.data.db.NewsEntity
import com.vereshchagin.nikolay.stankinschedule.news.core.data.mapper.toEntity
import com.vereshchagin.nikolay.stankinschedule.news.review.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow
import org.joda.time.DateTime
import retrofit2.await
import javax.inject.Inject

class NewsRepositoryImpl @Inject constructor(
    private val newsAPI: StankinNewsAPI,
    private val newsDB: RoomDatabase,
    private val newsDao: NewsDao,
    private val preference: PreferenceManager,
) : NewsRepository {

    override fun news(newsSubdivision: Int): PagingSource<Int, NewsEntity> {
        return newsDao.all(newsSubdivision)
    }

    override fun lastNews(newsCount: Int): Flow<List<NewsEntity>> {
        return newsDao.latest(newsCount)
    }

    override suspend fun addPostsIntoDb(
        newsSubdivision: Int,
        response: NewsResponse,
        refresh: Boolean,
    ) {
        val items = response.data.news

        if (items.isNotEmpty()) {
            newsDB.withTransaction {
                // если обновляем список
                if (refresh) {
                    newsDao.clear(newsSubdivision)
                }
                // индекс по порядку
                val start = newsDao.nextIndexInResponse(newsSubdivision)
                // добавление индекса и номера отдела
                val news = items.mapIndexed { index, news ->
                    news.toEntity(start + index, newsSubdivision)
                }

                newsDao.insert(news)
                preference.saveDateTime("news_$newsSubdivision", DateTime.now())
            }
        }
    }

    override suspend fun loadPage(newsSubdivision: Int, page: Int, count: Int): NewsResponse {
        return StankinNewsAPI.getNews(newsAPI, newsSubdivision, page, count).await()
    }

    override suspend fun refresh(newsSubdivision: Int, force: Boolean) {
        val lastRefresh = preference.getDateTime("news_$newsSubdivision")

        if (force || lastRefresh == null || lastRefresh subMinutes DateTime.now() > 30) {
            updateNews(newsSubdivision)
        }
    }

    private suspend fun updateNews(newsSubdivision: Int) {
        try {
            val response = loadPage(newsSubdivision, page = 1)
            addPostsIntoDb(newsSubdivision, response, refresh = true)
        } catch (ignored: Exception) {

        }
    }
}