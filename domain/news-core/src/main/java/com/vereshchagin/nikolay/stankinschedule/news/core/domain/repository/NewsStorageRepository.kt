package com.vereshchagin.nikolay.stankinschedule.news.core.domain.repository

import androidx.paging.PagingSource
import com.vereshchagin.nikolay.stankinschedule.news.core.domain.model.NewsPost
import kotlinx.coroutines.flow.Flow

interface NewsStorageRepository {

    fun news(newsSubdivision: Int): PagingSource<Int, NewsPost>

    fun lastNews(newsCount: Int = 3): Flow<List<NewsPost>>

    suspend fun saveNews(newsSubdivision: Int, posts: List<NewsPost>, force: Boolean = false)

    suspend fun clearNews(newsSubdivision: Int)
}