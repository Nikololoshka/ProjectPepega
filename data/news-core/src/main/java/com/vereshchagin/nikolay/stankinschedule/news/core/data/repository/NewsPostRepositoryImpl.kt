package com.vereshchagin.nikolay.stankinschedule.news.core.data.repository

import com.vereshchagin.nikolay.stankinschedule.core.data.cache.CacheManager
import com.vereshchagin.nikolay.stankinschedule.core.domain.cache.CacheContainer
import com.vereshchagin.nikolay.stankinschedule.news.core.data.api.StankinNewsAPI
import com.vereshchagin.nikolay.stankinschedule.news.core.data.mapper.toNewsContent
import com.vereshchagin.nikolay.stankinschedule.news.core.domain.model.NewsContent
import com.vereshchagin.nikolay.stankinschedule.news.core.domain.repository.NewsPostRepository
import retrofit2.await
import javax.inject.Inject

class NewsPostRepositoryImpl @Inject constructor(
    private val newsAPI: StankinNewsAPI,
    private val cache: CacheManager,
) : NewsPostRepository {

    init {
        cache.addStartedPath("news_posts")
    }

    override suspend fun saveNewsContent(news: NewsContent) {
        cache.saveToCache(news, generateName(postId = news.id))
    }

    override suspend fun loadNewsContent(postId: Int): CacheContainer<NewsContent>? {
        return cache.loadFromCache(NewsContent::class.java, generateName(postId = postId))
    }

    override suspend fun loadPost(postId: Int): NewsContent {
        val response = StankinNewsAPI.getNewsPost(newsAPI, postId).await()
        return response.data.toNewsContent()
    }

    private fun generateName(postId: Int): String = "post_$postId"
}