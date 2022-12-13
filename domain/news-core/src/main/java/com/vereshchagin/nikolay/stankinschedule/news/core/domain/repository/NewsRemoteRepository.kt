package com.vereshchagin.nikolay.stankinschedule.news.core.domain.repository

import com.vereshchagin.nikolay.stankinschedule.news.core.domain.model.NewsPost

interface NewsRemoteRepository {

    suspend fun loadPage(newsSubdivision: Int, page: Int, count: Int = 40): List<NewsPost>
}