package com.vereshchagin.nikolay.stankinschedule.ui.news.review.categories.paging

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.vereshchagin.nikolay.stankinschedule.BuildConfig
import com.vereshchagin.nikolay.stankinschedule.model.news.NewsItem
import com.vereshchagin.nikolay.stankinschedule.repository.NewsRepository
import retrofit2.HttpException
import java.io.IOException
import kotlin.math.roundToInt

/**
 * Источник постов новостей из интернета.
 */
@ExperimentalPagingApi
class NewsPostRemoteMediator(
    private val repository: NewsRepository
) : RemoteMediator<Int, NewsItem>() {

    override suspend fun initialize(): InitializeAction {
        return if (repository.isRequiredRefresh()) {
            InitializeAction.LAUNCH_INITIAL_REFRESH
        } else {
            InitializeAction.SKIP_INITIAL_REFRESH
        }
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, NewsItem>
    ): MediatorResult {
        return try {
            val page = (when (loadType) {
                LoadType.REFRESH -> null
                LoadType.PREPEND -> return MediatorResult.Success(true)
                LoadType.APPEND -> state.lastItemOrNull()?.indexInResponse?.div(state.config.pageSize.toDouble())
            } ?: 0.0).roundToInt() + 1

            val response = repository.news(page, state.config.pageSize)
            repository.addPostsIntoDb(response, loadType == LoadType.REFRESH)

            if (BuildConfig.DEBUG) {
                Log.d(
                    "NewsPostRemoteLog",
                    "load: index=${state.lastItemOrNull()?.indexInResponse}, " +
                        "sub=${repository.newsSubdivision}, " +
                        "page=$page, " +
                        "size=${state.config.pageSize}"
                )
            }
            MediatorResult.Success(response.data.news.isEmpty())

        } catch (e: IOException) {
            MediatorResult.Error(e)

        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }
}