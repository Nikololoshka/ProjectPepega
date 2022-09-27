package com.vereshchagin.nikolay.stankinschedule.news.home.domain.usecase

import com.vereshchagin.nikolay.stankinschedule.news.core.data.mapper.toPost
import com.vereshchagin.nikolay.stankinschedule.news.core.domain.model.NewsPost
import com.vereshchagin.nikolay.stankinschedule.news.core.domain.repository.NewsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class NewsHomeUseCase @Inject constructor(
    private val repository: NewsRepository
) {
    fun news(newsCount: Int = 20): Flow<List<NewsPost>> =
        repository.lastNews(newsCount = newsCount)
            .map { news -> news.map { it.toPost() } }
            .flowOn(Dispatchers.IO)
}