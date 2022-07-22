package com.vereshchagin.nikolay.stankinschedule.news.domain.usecase

import com.vereshchagin.nikolay.stankinschedule.core.ui.State
import com.vereshchagin.nikolay.stankinschedule.news.data.mapper.toNewsContent
import com.vereshchagin.nikolay.stankinschedule.news.domain.repository.PostRepository
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class NewsViewerUseCase @Inject constructor(
    private val repository: PostRepository,
) {

    fun loadNewsContent(postId: Int) = flow {
        emit(State.loading())

        val cache = repository.loadNewsContent(postId)
        if (cache != null) {
            emit(State.success(cache.data))
            return@flow
        }

        val response = repository.loadPost(postId)
        val newsContent = response.data.toNewsContent()
        repository.saveNewsContent(newsContent)

        emit(State.success(newsContent))
    }
}