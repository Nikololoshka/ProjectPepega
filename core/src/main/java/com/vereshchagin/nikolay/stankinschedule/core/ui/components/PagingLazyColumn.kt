package com.vereshchagin.nikolay.stankinschedule.core.ui.components

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.items

@Composable
fun <T : Any> PagingLazyColumn(
    state: LazyListState,
    pagingItems: LazyPagingItems<T>,
    modifier: Modifier = Modifier,
    key: ((item: T) -> Any)? = null,
    onContent: @Composable LazyItemScope.(value: T?) -> Unit,
    onContentLoading: @Composable LazyItemScope.() -> Unit,
    onContentError: @Composable LazyItemScope.(error: Throwable) -> Unit,
    onAppendLoading: @Composable LazyItemScope.() -> Unit,
    onAppendError: @Composable LazyItemScope.(error: Throwable) -> Unit,
) {
    LazyColumn(
        state = state,
        modifier = modifier
    ) {
        items(
            items = pagingItems,
            key = key,
            itemContent = onContent
        )
        val loadState = pagingItems.loadState
        when {
            loadState.refresh is LoadState.Loading -> {
                item {
                    onContentLoading()
                }
            }
            loadState.refresh is LoadState.Error -> {
                item {
                    onContentError((loadState.refresh as LoadState.Error).error)
                }
            }
            loadState.append is LoadState.Loading -> {
                item {
                    onAppendLoading()
                }
            }
            loadState.append is LoadState.Error -> {
                item {
                    onAppendError((loadState.append as LoadState.Error).error)
                }
            }
        }
    }
}