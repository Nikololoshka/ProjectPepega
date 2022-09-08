package com.vereshchagin.nikolay.stankinschedule.news.review.data.source

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.vereshchagin.nikolay.stankinschedule.news.core.data.db.NewsEntity
import com.vereshchagin.nikolay.stankinschedule.news.review.BuildConfig
import com.vereshchagin.nikolay.stankinschedule.news.review.domain.repository.NewsRepository
import kotlin.math.roundToInt

@OptIn(ExperimentalPagingApi::class)
class NewsRemoteSource(
    private val newsSubdivision: Int,
    private val repository: NewsRepository,
) : RemoteMediator<Int, NewsEntity>() {

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.SKIP_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, NewsEntity>,
    ): MediatorResult {
        return try {
            val page = (when (loadType) {
                LoadType.REFRESH -> null
                LoadType.PREPEND -> return MediatorResult.Success(true)
                LoadType.APPEND -> state.lastItemOrNull()?.indexOrder?.div(state.config.pageSize.toDouble())
            } ?: 0.0).roundToInt() + 1

            if (BuildConfig.DEBUG) {
                Log.d("NewsRemoteSource", "Type=$loadType, page=$page")
            }

            val response = repository.loadPage(newsSubdivision, page, state.config.pageSize)
            repository.addPostsIntoDb(newsSubdivision, response, loadType == LoadType.REFRESH)

            MediatorResult.Success(response.data.news.isEmpty())

        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }

}