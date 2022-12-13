package com.vereshchagin.nikolay.stankinschedule.news.core.data.source

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.vereshchagin.nikolay.stankinschedule.news.core.domain.model.NewsPost
import com.vereshchagin.nikolay.stankinschedule.news.core.domain.repository.NewsRemoteRepository
import com.vereshchagin.nikolay.stankinschedule.news.core.domain.repository.NewsStorageRepository
import kotlin.math.roundToInt

@OptIn(ExperimentalPagingApi::class)
class NewsRemoteSource(
    private val newsSubdivision: Int,
    private val remoteRepository: NewsRemoteRepository,
    private val storageRepository: NewsStorageRepository
) : RemoteMediator<Int, NewsPost>() {

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.SKIP_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, NewsPost>,
    ): MediatorResult {
        return try {
            val page = (when (loadType) {
                LoadType.REFRESH -> null
                LoadType.PREPEND -> return MediatorResult.Success(true)
                LoadType.APPEND -> state.anchorPosition?.div(state.config.pageSize.toDouble())
            } ?: 0.0).roundToInt() + 1

            val response = remoteRepository.loadPage(newsSubdivision, page, state.config.pageSize)
            storageRepository.saveNews(newsSubdivision, response, loadType == LoadType.REFRESH)

            MediatorResult.Success(response.isEmpty())

        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }
}