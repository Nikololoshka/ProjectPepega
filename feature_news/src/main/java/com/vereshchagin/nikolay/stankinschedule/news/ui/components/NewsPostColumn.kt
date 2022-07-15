package com.vereshchagin.nikolay.stankinschedule.news.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import coil.ImageLoader
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.vereshchagin.nikolay.stankinschedule.core.ui.components.PagingLoader
import com.vereshchagin.nikolay.stankinschedule.news.domain.model.NewsPost
import kotlinx.coroutines.flow.Flow


@Composable
fun NewsPostColumn(
    posts: Flow<PagingData<NewsPost>>,
    onClick: (post: NewsPost) -> Unit,
    isNewsRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    imageLoader: ImageLoader = defaultImageLoader(LocalContext.current),
) {
    val lazyPostItems: LazyPagingItems<NewsPost> = posts.collectAsLazyPagingItems()
    val lazyColumnState = rememberLazyListState()

    val refreshingState = rememberSwipeRefreshState(isNewsRefreshing)

    SwipeRefresh(
        state = refreshingState,
        onRefresh = onRefresh
    ) {
        LazyColumn(
            state = lazyColumnState,
            modifier = modifier
        ) {
            items(lazyPostItems) { post ->
                NewsPost(
                    post = post!!, // null - placeholder support
                    imageLoader = imageLoader,
                    onClick = onClick,
                    modifier = Modifier.padding(8.dp)
                )
                Divider()
            }

            PagingLoader(
                loadState = lazyPostItems.loadState,
                onContentLoading = {
                    item {
                        Box(
                            modifier = Modifier.fillParentMaxSize()
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }
                },
                onContentError = {

                },
                onAppendLoading = {

                },
                onAppendError = {

                }
            )
        }
    }
}