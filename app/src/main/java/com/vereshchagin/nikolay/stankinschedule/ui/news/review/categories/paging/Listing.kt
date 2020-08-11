package com.vereshchagin.nikolay.stankinschedule.ui.news.review.categories.paging

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import com.vereshchagin.nikolay.stankinschedule.api.NetworkState

/**
 * Вспомогательный класс для отображения данных.
 */
class Listing<T>(
    val pagedList: LiveData<PagedList<T>>,
    val networkState: LiveData<NetworkState>,
    val refreshState: LiveData<NetworkState>,
    val refresh: () -> Unit,
    val retry: () -> Unit
)
