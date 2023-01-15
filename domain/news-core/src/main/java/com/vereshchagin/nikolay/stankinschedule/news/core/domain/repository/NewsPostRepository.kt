package com.vereshchagin.nikolay.stankinschedule.news.core.domain.repository

import com.vereshchagin.nikolay.stankinschedule.core.domain.cache.CacheContainer
import com.vereshchagin.nikolay.stankinschedule.news.core.domain.model.NewsContent

interface NewsPostRepository {

    suspend fun saveNewsContent(news: NewsContent)

    suspend fun loadNewsContent(postId: Int): CacheContainer<NewsContent>?

    suspend fun loadPost(postId: Int): NewsContent

}