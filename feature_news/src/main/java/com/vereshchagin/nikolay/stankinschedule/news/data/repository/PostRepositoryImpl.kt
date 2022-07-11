package com.vereshchagin.nikolay.stankinschedule.news.data.repository

import com.vereshchagin.nikolay.stankinschedule.news.data.api.PostResponse
import com.vereshchagin.nikolay.stankinschedule.news.data.api.StankinNewsAPI
import com.vereshchagin.nikolay.stankinschedule.news.domain.model.NewsContent
import com.vereshchagin.nikolay.stankinschedule.news.domain.repository.PostRepository
import retrofit2.await
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor(
    private val newsAPI: StankinNewsAPI,
) : PostRepository {

    override suspend fun saveNewsContent(news: NewsContent) {

    }

    override suspend fun loadNewsContent(postId: Int): PostResponse {
        return StankinNewsAPI.getNewsPost(newsAPI, postId).await()
    }
}