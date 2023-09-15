package com.vereshchagin.nikolay.stankinschedule.news.review.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil.ImageLoader
import com.vereshchagin.nikolay.stankinschedule.core.ui.components.PagingLazyColumn
import com.vereshchagin.nikolay.stankinschedule.core.ui.theme.Dimen
import com.vereshchagin.nikolay.stankinschedule.core.ui.utils.newsImageLoader
import com.vereshchagin.nikolay.stankinschedule.news.core.domain.model.NewsPost
import kotlinx.coroutines.flow.Flow


@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
fun NewsPostColumn(
    posts: Flow<PagingData<NewsPost>>,
    onClick: (post: NewsPost) -> Unit,
    isNewsRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    imageLoader: ImageLoader = newsImageLoader(LocalContext.current),
    columnState: LazyListState = rememberLazyListState()
) {
    val lazyPostItems: LazyPagingItems<NewsPost> = posts.collectAsLazyPagingItems()

    val refreshingState = rememberPullRefreshState(
        refreshing = isNewsRefreshing,
        onRefresh = onRefresh
    )

    Box(
        modifier = modifier.pullRefresh(refreshingState)
    ) {
        PagingLazyColumn(
            state = columnState,
            pagingItems = lazyPostItems,
            modifier = Modifier.fillMaxSize(),
            key = lazyPostItems.itemKey { it.id },
            onContent = { index ->
                val post = lazyPostItems[index]
                NewsPost(
                    post = post,
                    imageLoader = imageLoader,
                    onClick = onClick,
                    modifier = Modifier
                        .padding(8.dp)
                        .animateItemPlacement()
                )
                Divider()
            },
            onContentLoading = {
                NewsLoading(modifier = Modifier.fillParentMaxWidth())
            },
            onContentError = { throwable ->
                NewsError(
                    error = throwable,
                    onRetry = { lazyPostItems.retry() },
                    modifier = Modifier.fillParentMaxWidth()
                )
            },
            onAppendLoading = {
                NewsLoading(
                    modifier = Modifier
                        .fillParentMaxWidth()
                        .padding(Dimen.ContentPadding)
                )
            },
            onAppendError = { throwable ->
                NewsError(
                    error = throwable,
                    onRetry = { lazyPostItems.retry() },
                    modifier = Modifier
                        .fillParentMaxWidth()
                        .padding(Dimen.ContentPadding)
                )
            }
        )

        PullRefreshIndicator(
            refreshing = isNewsRefreshing,
            state = refreshingState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}