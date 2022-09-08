package com.vereshchagin.nikolay.stankinschedule.news.viewer.domain.usecase

import com.vereshchagin.nikolay.stankinschedule.core.ui.UIState
import com.vereshchagin.nikolay.stankinschedule.core.ui.subHours
import com.vereshchagin.nikolay.stankinschedule.news.core.data.mapper.toNewsContent
import com.vereshchagin.nikolay.stankinschedule.news.core.domain.model.NewsContent
import com.vereshchagin.nikolay.stankinschedule.news.viewer.domain.repository.PostRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.joda.time.DateTime
import javax.inject.Inject

class NewsViewerUseCase @Inject constructor(
    private val repository: PostRepository,
) {

    fun loadNewsContent(postId: Int, force: Boolean = false): Flow<UIState<NewsContent>> = flow {
        emit(UIState.loading())

        val cache = repository.loadNewsContent(postId)
        if (cache != null && !force && (cache.cacheTime subHours DateTime.now() < 24)) {
            emit(UIState.success(cache.data))
            return@flow
        }

        val response = repository.loadPost(postId)
        val newsContent = response.data.toNewsContent()
        repository.saveNewsContent(newsContent)

        emit(UIState.success(newsContent))
    }
}