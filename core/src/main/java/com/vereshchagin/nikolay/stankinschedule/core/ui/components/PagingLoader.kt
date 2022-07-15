package com.vereshchagin.nikolay.stankinschedule.core.ui.components

import androidx.compose.foundation.lazy.LazyListScope
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState


fun LazyListScope.PagingLoader(
    loadState: CombinedLoadStates,
    onContentLoading: LazyListScope.() -> Unit,
    onContentError: LazyListScope.() -> Unit,
    onAppendLoading: LazyListScope.() -> Unit,
    onAppendError: LazyListScope.() -> Unit,
) {
    when {
        loadState.refresh is LoadState.Loading -> onContentLoading()
        loadState.refresh is LoadState.Error -> onContentError()
        loadState.append is LoadState.Loading -> onAppendLoading()
        loadState.append is LoadState.Error -> onAppendError()
    }
}