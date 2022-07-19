package com.vereshchagin.nikolay.stankinschedule.news.data.repository

import com.vereshchagin.nikolay.stankinschedule.core.ui.CacheManager
import com.vereshchagin.nikolay.stankinschedule.news.data.api.PostResponse
import com.vereshchagin.nikolay.stankinschedule.news.data.api.StankinNewsAPI
import com.vereshchagin.nikolay.stankinschedule.news.domain.model.NewsContent
import com.vereshchagin.nikolay.stankinschedule.news.domain.repository.PostRepository
import retrofit2.await
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor(
    private val newsAPI: StankinNewsAPI,
    private val cache: CacheManager
) : PostRepository {

    init {
        cache.addStartedPath("news_posts")
    }

    override suspend fun saveNewsContent(news: NewsContent) {
        cache.saveToCache(news, generateName(postId = news.id))
    }

    override suspend fun loadNewsContent(postId: Int): NewsContent? {
        return cache.loadFromCache(NewsContent::class.java, generateName(postId = postId))
    }

    override suspend fun loadPost(postId: Int): PostResponse {
        return StankinNewsAPI.getNewsPost(newsAPI, postId).await()
    }

    private fun generateName(postId: Int): String = "post_$postId"
}