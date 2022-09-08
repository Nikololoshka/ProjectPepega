package com.vereshchagin.nikolay.stankinschedule.news.viewer.domain.repository

import com.vereshchagin.nikolay.stankinschedule.core.cache.CacheContainer
import com.vereshchagin.nikolay.stankinschedule.news.core.data.api.PostResponse
import com.vereshchagin.nikolay.stankinschedule.news.core.domain.model.NewsContent

interface PostRepository {

    suspend fun saveNewsContent(news: NewsContent)

    suspend fun loadNewsContent(postId: Int): CacheContainer<NewsContent>?

    suspend fun loadPost(postId: Int): PostResponse

}