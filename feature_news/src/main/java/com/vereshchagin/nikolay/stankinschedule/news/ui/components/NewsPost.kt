package com.vereshchagin.nikolay.stankinschedule.news.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.disk.DiskCache
import coil.memory.MemoryCache
import com.vereshchagin.nikolay.stankinschedule.news.data.db.NewsEntity

@Preview(showBackground = true)
@Composable
fun NewsPostPreview() {
    NewsPost(
        post = NewsEntity(
            0, 0, 0, "Example title.", "07.07.22", ""
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    )
}

@Composable
fun NewsPost(
    post: NewsEntity,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AsyncImage(
            model = post.logo,
            imageLoader = ImageLoader(LocalContext.current).newBuilder()
                .crossfade(true)
                .memoryCache(
                    MemoryCache.Builder(LocalContext.current)
                        .maxSizePercent(0.2)
                        .build()
                )
                .diskCache(
                    DiskCache.Builder()
                        .maximumMaxSizeBytes(1024 * 1024 * 64)
                        .build()
                )
                .build(),
            contentDescription = null,
            modifier = Modifier
                .size(100.dp, 60.dp)
                .align(Alignment.CenterVertically)
        )
        Column(
            modifier = Modifier
                .weight(weight = 1f)
                .defaultMinSize(minHeight = 60.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = post.title,
                overflow = TextOverflow.Ellipsis,
                maxLines = 2
            )
            Text(
                text = post.date,
                modifier = Modifier
                    .align(Alignment.End)
            )
        }
    }
}