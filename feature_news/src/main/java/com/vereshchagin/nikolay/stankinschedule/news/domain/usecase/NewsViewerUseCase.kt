package com.vereshchagin.nikolay.stankinschedule.news.domain.usecase

import com.vereshchagin.nikolay.stankinschedule.core.ui.State
import com.vereshchagin.nikolay.stankinschedule.core.ui.subHours
import com.vereshchagin.nikolay.stankinschedule.news.data.mapper.toNewsContent
import com.vereshchagin.nikolay.stankinschedule.news.domain.repository.PostRepository
import kotlinx.coroutines.flow.flow
import org.joda.time.DateTime
import javax.inject.Inject

class NewsViewerUseCase @Inject constructor(
    private val repository: PostRepository,
) {

    fun loadNewsContent(postId: Int, force: Boolean = false) = flow {
        emit(State.loading())

        val cache = repository.loadNewsContent(postId)
        if (cache != null && !force && (cache.cacheTime subHours DateTime.now() < 24)) {
            emit(State.success(cache.data))
            return@flow
        }

        val response = repository.loadPost(postId)
        val newsContent = response.data.toNewsContent()
        repository.saveNewsContent(newsContent)

        emit(State.success(newsContent))
    }
}