package com.vereshchagin.nikolay.stankinschedule.news.ui.review.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.ImageLoader
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.vereshchagin.nikolay.stankinschedule.core.ui.components.PagingLazyColumn
import com.vereshchagin.nikolay.stankinschedule.core.ui.theme.Dimen
import com.vereshchagin.nikolay.stankinschedule.news.domain.model.NewsPost
import com.vereshchagin.nikolay.stankinschedule.news.ui.components.NewsError
import com.vereshchagin.nikolay.stankinschedule.news.ui.components.NewsLoading
import com.vereshchagin.nikolay.stankinschedule.news.utils.newsImageLoader
import kotlinx.coroutines.flow.Flow


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NewsPostColumn(
    posts: Flow<PagingData<NewsPost>>,
    onClick: (post: NewsPost) -> Unit,
    isNewsRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    imageLoader: ImageLoader = newsImageLoader(LocalContext.current),
) {
    val lazyPostItems: LazyPagingItems<NewsPost> = posts.collectAsLazyPagingItems()
    val lazyColumnState = rememberLazyListState()

    val refreshingState = rememberSwipeRefreshState(isNewsRefreshing)

    SwipeRefresh(
        state = refreshingState,
        onRefresh = onRefresh
    ) {
        PagingLazyColumn(
            state = lazyColumnState,
            pagingItems = lazyPostItems,
            modifier = modifier,
            onContent = { post ->
                NewsPost(
                    post = post!!, // null - placeholder support
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
    }
}