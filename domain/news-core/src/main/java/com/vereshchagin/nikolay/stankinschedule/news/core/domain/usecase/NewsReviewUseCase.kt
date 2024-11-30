package com.vereshchagin.nikolay.stankinschedule.news.core.domain.usecase

import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingSource
import androidx.paging.RemoteMediator
import com.vereshchagin.nikolay.stankinschedule.core.domain.ext.subMinutes
import com.vereshchagin.nikolay.stankinschedule.news.core.domain.BuildConfig
import com.vereshchagin.nikolay.stankinschedule.news.core.domain.model.NewsPost
import com.vereshchagin.nikolay.stankinschedule.news.core.domain.model.NewsSubdivision
import com.vereshchagin.nikolay.stankinschedule.news.core.domain.repository.NewsMediatorRepository
import com.vereshchagin.nikolay.stankinschedule.news.core.domain.repository.NewsPreferenceRepository
import com.vereshchagin.nikolay.stankinschedule.news.core.domain.repository.NewsRemoteRepository
import com.vereshchagin.nikolay.stankinschedule.news.core.domain.repository.NewsStorageRepository
import kotlinx.coroutines.flow.Flow
import org.joda.time.DateTime
import javax.inject.Inject

class NewsReviewUseCase @Inject constructor(
    private val storageRepository: NewsStorageRepository,
    private val remoteRepository: NewsRemoteRepository,
    private val preferenceRepository: NewsPreferenceRepository,
    private val mediatorRepository: NewsMediatorRepository
) {

    fun news(subdivision: Int): PagingSource<Int, NewsPost> =
        storageRepository.news(subdivision)

    @OptIn(ExperimentalPagingApi::class)
    fun newsMediator(subdivision: Int): RemoteMediator<Int, NewsPost> =
        mediatorRepository.newsMediator(subdivision)

    fun lastNews(newsCount: Int): Flow<List<NewsPost>> = storageRepository.lastNews(newsCount)

    suspend fun refreshAllNews(force: Boolean = false) {
        // for (subdivision in NewsSubdivision.values()) {
        //    refreshNews(subdivision.id, force)
        // }
        refreshNews(NewsSubdivision.University.id, force)
    }

    suspend fun refreshNews(subdivision: Int, force: Boolean = false) {
        val lastRefresh = preferenceRepository.currentNewsDateTime(subdivision)

        if (BuildConfig.DEBUG || force || lastRefresh == null || lastRefresh subMinutes DateTime.now() > 30) {
            val posts = remoteRepository.loadPage(subdivision, page = 1)
            storageRepository.saveNews(subdivision, posts, true)
            preferenceRepository.updateNewsDateTime(subdivision)
        }
    }
}