package com.vereshchagin.nikolay.stankinschedule.news.core.data.repository

import com.vereshchagin.nikolay.stankinschedule.news.core.data.api.StankinNewsAPI
import com.vereshchagin.nikolay.stankinschedule.news.core.data.mapper.toPost
import com.vereshchagin.nikolay.stankinschedule.news.core.domain.model.NewsPost
import com.vereshchagin.nikolay.stankinschedule.news.core.domain.repository.NewsRemoteRepository
import retrofit2.await
import javax.inject.Inject


class NewsRemoteRepositoryImpl @Inject constructor(
    private val newsAPI: StankinNewsAPI,
) : NewsRemoteRepository {

    override suspend fun loadPage(
        newsSubdivision: Int,
        page: Int,
        count: Int
    ): List<NewsPost> {
        val response = StankinNewsAPI.getNews(newsAPI, newsSubdivision, page, count).await()
        return response.data.news.map { it.toPost() }
    }
}