package com.vereshchagin.nikolay.stankinschedule.news.viewer.data.repository

import com.vereshchagin.nikolay.stankinschedule.core.cache.CacheContainer
import com.vereshchagin.nikolay.stankinschedule.core.cache.CacheManager
import com.vereshchagin.nikolay.stankinschedule.news.core.data.api.PostResponse
import com.vereshchagin.nikolay.stankinschedule.news.core.data.api.StankinNewsAPI
import com.vereshchagin.nikolay.stankinschedule.news.core.domain.model.NewsContent
import com.vereshchagin.nikolay.stankinschedule.news.viewer.domain.repository.PostRepository
import retrofit2.await
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor(
    private val newsAPI: StankinNewsAPI,
    private val cache: CacheManager,
) : PostRepository {

    init {
        cache.addStartedPath("news_posts")
    }

    override suspend fun saveNewsContent(news: NewsContent) {
        cache.saveToCache(news, generateName(postId = news.id))
    }

    override suspend fun loadNewsContent(postId: Int): CacheContainer<NewsContent>? {
        return cache.loadFromCache(NewsContent::class.java, generateName(postId = postId))
    }

    override suspend fun loadPost(postId: Int): PostResponse {
        return StankinNewsAPI.getNewsPost(newsAPI, postId).await()
    }

    private fun generateName(postId: Int): String = "post_$postId"
}