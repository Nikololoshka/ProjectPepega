package com.vereshchagin.nikolay.stankinschedule.news.ui.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.vereshchagin.nikolay.stankinschedule.news.data.db.NewsEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@Preview(showBackground = true)
@Composable
fun NewsPostColumnPreview() {
    NewsPostColumn(
        posts = flowOf(
            PagingData.from(
                MutableList(10) { i ->
                    NewsEntity(i, i, 0, "Example title #$i", "07.07.22", "")
                }
            )
        ),
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
fun NewsPostColumn(
    posts: Flow<PagingData<NewsEntity>>,
    modifier: Modifier = Modifier,
) {
    val lazyPostItems: LazyPagingItems<NewsEntity> = posts.collectAsLazyPagingItems()

    LazyColumn(modifier = modifier) {
        items(lazyPostItems) { post ->
            NewsPost(
                post = post!!, // null - placeholder support
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}