package com.vereshchagin.nikolay.stankinschedule.news.core.domain.usecase

import com.vereshchagin.nikolay.stankinschedule.core.domain.ext.subHours
import com.vereshchagin.nikolay.stankinschedule.news.core.domain.model.NewsContent
import com.vereshchagin.nikolay.stankinschedule.news.core.domain.repository.NewsPostRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.joda.time.DateTime
import javax.inject.Inject

class NewsViewerUseCase @Inject constructor(
    private val repository: NewsPostRepository,
) {

    fun loadNewsContent(postId: Int, force: Boolean = false): Flow<NewsContent> = flow {

        val cache = repository.loadNewsContent(postId)
        if (cache != null && !force && (cache.cacheTime subHours DateTime.now() < 24)) {
            emit(cache.data)
            return@flow
        }

        val newsContent = repository.loadPost(postId)
        repository.saveNewsContent(newsContent)

        emit(newsContent)
    }
}