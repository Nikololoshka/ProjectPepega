package com.vereshchagin.nikolay.stankinschedule.ui.news.review.categories.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.vereshchagin.nikolay.stankinschedule.model.news.NewsPost

/**
 * Источник постов новостей из интернета.
 */
@ExperimentalPagingApi
class NewsPostRemoteMediator : RemoteMediator<Int, NewsPost>() {
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, NewsPost>
    ): MediatorResult {
        TODO("Not yet implemented")
    }
}