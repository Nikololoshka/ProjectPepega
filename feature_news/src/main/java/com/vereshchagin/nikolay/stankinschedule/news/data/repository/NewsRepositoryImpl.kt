package com.vereshchagin.nikolay.stankinschedule.news.data.repository

import androidx.paging.PagingSource
import androidx.room.RoomDatabase
import androidx.room.withTransaction
import com.vereshchagin.nikolay.stankinschedule.news.data.api.NewsResponse
import com.vereshchagin.nikolay.stankinschedule.news.data.api.StankinNewsAPI
import com.vereshchagin.nikolay.stankinschedule.news.data.db.NewsDao
import com.vereshchagin.nikolay.stankinschedule.news.data.db.NewsEntity
import com.vereshchagin.nikolay.stankinschedule.news.data.mapper.toEntity
import com.vereshchagin.nikolay.stankinschedule.news.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow
import retrofit2.await
import javax.inject.Inject

class NewsRepositoryImpl @Inject constructor(
    private val newsAPI: StankinNewsAPI,
    private val newsDB: RoomDatabase,
    private val newsDao: NewsDao,
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
            }
        }
    }

    override suspend fun loadPage(newsSubdivision: Int, page: Int, count: Int): NewsResponse {
        return StankinNewsAPI.getNews(newsAPI, newsSubdivision, page, count).await()
    }

    override suspend fun update(newsSubdivision: Int) {

    }
}