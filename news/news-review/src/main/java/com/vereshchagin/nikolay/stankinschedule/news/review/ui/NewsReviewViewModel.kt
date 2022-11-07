package com.vereshchagin.nikolay.stankinschedule.news.review.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.vereshchagin.nikolay.stankinschedule.news.core.data.mapper.toPost
import com.vereshchagin.nikolay.stankinschedule.news.core.domain.model.NewsPost
import com.vereshchagin.nikolay.stankinschedule.news.core.domain.repository.NewsRepository
import com.vereshchagin.nikolay.stankinschedule.news.review.data.source.NewsRemoteSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
@OptIn(ExperimentalPagingApi::class)
class NewsReviewViewModel @Inject constructor(
    private val repository: NewsRepository,
) : ViewModel() {

    private val newsPagers = mutableMapOf<Int, Flow<PagingData<NewsPost>>>()
    private val newsRefreshing = mutableMapOf<Int, MutableStateFlow<Boolean>>()

    private fun pagerForSubdivision(newsSubdivision: Int): Flow<PagingData<NewsPost>> {
        // Обновляем новости
        refreshNews(newsSubdivision, force = false)

        return Pager(
            config = PagingConfig(
                pageSize = 40,
                enablePlaceholders = false,
                initialLoadSize = 40
            ),
            remoteMediator = NewsRemoteSource(
                newsSubdivision = newsSubdivision,
                repository = repository
            ),
            pagingSourceFactory = { repository.news(newsSubdivision) }
        ).flow.map { posts -> posts.map { post -> post.toPost() } }
            .cachedIn(viewModelScope)
    }

    fun news(newsSubdivision: Int): Flow<PagingData<NewsPost>> {
        return newsPagers.getOrPut(newsSubdivision) {
            pagerForSubdivision(newsSubdivision)
        }
    }

    private fun stateForNewsRefreshing(newsSubdivision: Int): MutableStateFlow<Boolean> {
        return newsRefreshing.getOrPut(newsSubdivision) {
            MutableStateFlow(false)
        }
    }

    fun newsRefreshing(newsSubdivision: Int): StateFlow<Boolean> {
        return stateForNewsRefreshing(newsSubdivision)
    }

    fun refreshNews(newsSubdivision: Int, force: Boolean) {
        viewModelScope.launch {
            val isRefreshing = stateForNewsRefreshing(newsSubdivision)

            isRefreshing.value = true
            repository.refresh(newsSubdivision, force)
            isRefreshing.value = false
        }
    }
}
