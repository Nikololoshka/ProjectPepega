package com.vereshchagin.nikolay.stankinschedule.news.domain.repository

import com.vereshchagin.nikolay.stankinschedule.news.data.api.PostResponse
import com.vereshchagin.nikolay.stankinschedule.news.domain.model.NewsContent

interface PostRepository {

    suspend fun saveNewsContent(news: NewsContent)

    suspend fun loadNewsContent(postId: Int): NewsContent?

    suspend fun loadPost(postId: Int): PostResponse

}