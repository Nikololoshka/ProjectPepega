package com.vereshchagin.nikolay.stankinschedule.news.data.repository

import androidx.paging.PagingSource
import com.vereshchagin.nikolay.stankinschedule.news.data.api.NewsResponse
import com.vereshchagin.nikolay.stankinschedule.news.data.api.StankinNewsAPI
import com.vereshchagin.nikolay.stankinschedule.news.data.db.NewsDao
import com.vereshchagin.nikolay.stankinschedule.news.data.db.NewsEntity
import com.vereshchagin.nikolay.stankinschedule.news.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow
import retrofit2.await

class NewsRepositoryImpl(
    private val newsAPI: StankinNewsAPI,
    private val newsDao: NewsDao,
) : NewsRepository {

    override fun news(newsSubdivision: Int): PagingSource<Int, NewsEntity> {
        return newsDao.all(newsSubdivision)
    }

    override fun lastNews(newsCount: Int): Flow<List<NewsEntity>> {
        return newsDao.latest(newsCount)
    }

    override suspend fun loadPage(newsSubdivision: Int, page: Int, count: Int): NewsResponse {
        return StankinNewsAPI.getNews(newsAPI, newsSubdivision, page, count).await()
    }

    override suspend fun update(newsSubdivision: Int) {

    }
}