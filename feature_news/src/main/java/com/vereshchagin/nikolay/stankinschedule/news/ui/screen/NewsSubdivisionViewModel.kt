package com.vereshchagin.nikolay.stankinschedule.news.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.vereshchagin.nikolay.stankinschedule.news.data.mapper.toPost
import com.vereshchagin.nikolay.stankinschedule.news.data.source.NewsRemoteSource
import com.vereshchagin.nikolay.stankinschedule.news.domain.model.NewsPost
import com.vereshchagin.nikolay.stankinschedule.news.domain.repository.NewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject


@HiltViewModel
@OptIn(ExperimentalPagingApi::class)
class NewsSubdivisionViewModel @Inject constructor(
    private val repository: NewsRepository,
) : ViewModel() {

    private val newsPagers = mutableMapOf<Int, Flow<PagingData<NewsPost>>>()

    private fun pagerForSubdivision(newsSubdivision: Int): Flow<PagingData<NewsPost>> {
        return Pager(
            config = PagingConfig(
                pageSize = 40
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
}
