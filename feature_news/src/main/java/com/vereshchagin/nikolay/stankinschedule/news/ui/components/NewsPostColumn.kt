package com.vereshchagin.nikolay.stankinschedule.news.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import coil.ImageLoader
import com.vereshchagin.nikolay.stankinschedule.news.domain.model.NewsPost
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@Preview(showBackground = true)
@Composable
fun NewsPostColumnPreview() {
    NewsPostColumn(
        posts = flowOf(
            PagingData.from(
                MutableList(10) { i ->
                    NewsPost(i, "Example title #$i", null, "07.07.22")
                }
            )
        ),
        onClick = {},
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
fun NewsPostColumn(
    posts: Flow<PagingData<NewsPost>>,
    onClick: (post: NewsPost) -> Unit,
    modifier: Modifier = Modifier,
    imageLoader: ImageLoader = defaultImageLoader(LocalContext.current),
) {
    val lazyPostItems: LazyPagingItems<NewsPost> = posts.collectAsLazyPagingItems()
    val lazyColumnState = rememberLazyListState()

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