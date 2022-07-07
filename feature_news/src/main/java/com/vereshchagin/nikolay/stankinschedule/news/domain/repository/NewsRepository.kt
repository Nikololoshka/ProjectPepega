package com.vereshchagin.nikolay.stankinschedule.news.domain.repository

import androidx.paging.PagingSource
import com.vereshchagin.nikolay.stankinschedule.news.data.api.NewsResponse
import com.vereshchagin.nikolay.stankinschedule.news.data.db.NewsEntity
import kotlinx.coroutines.flow.Flow

interface NewsRepository {

    fun news(newsSubdivision: Int): PagingSource<Int, NewsEntity>

    fun lastNews(newsCount: Int = 3): Flow<List<NewsEntity>>

    suspend fun loadPage(newsSubdivision: Int, page: Int, count: Int = 40): NewsResponse

    suspend fun update(newsSubdivision: Int)
}