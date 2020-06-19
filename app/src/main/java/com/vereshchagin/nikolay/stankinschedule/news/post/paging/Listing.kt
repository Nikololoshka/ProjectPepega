package com.vereshchagin.nikolay.stankinschedule.news.post.paging

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import com.vereshchagin.nikolay.stankinschedule.news.network.NetworkState

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
