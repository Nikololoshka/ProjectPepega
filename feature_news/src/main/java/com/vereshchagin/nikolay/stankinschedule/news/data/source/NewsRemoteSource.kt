package com.vereshchagin.nikolay.stankinschedule.news.data.source

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.vereshchagin.nikolay.stankinschedule.news.data.db.NewsEntity
import com.vereshchagin.nikolay.stankinschedule.news.domain.repository.NewsRepository
import retrofit2.HttpException
import kotlin.math.roundToInt

@OptIn(ExperimentalPagingApi::class)
class NewsRemoteSource(
    private val newsSubdivision: Int,
    private val repository: NewsRepository,
) : RemoteMediator<Int, NewsEntity>() {

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

            val response = repository.loadPage(newsSubdivision, page, state.config.pageSize)
            repository.addPostsIntoDb(newsSubdivision, response, loadType == LoadType.REFRESH)

            MediatorResult.Success(response.data.news.isEmpty())

        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }

}