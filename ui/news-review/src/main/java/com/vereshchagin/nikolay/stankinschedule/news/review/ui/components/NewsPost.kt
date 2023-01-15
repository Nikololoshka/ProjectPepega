package com.vereshchagin.nikolay.stankinschedule.news.review.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.AsyncImage
import com.vereshchagin.nikolay.stankinschedule.core.domain.ext.formatDate
import com.vereshchagin.nikolay.stankinschedule.core.ui.theme.AppTheme
import com.vereshchagin.nikolay.stankinschedule.core.ui.theme.Dimen
import com.vereshchagin.nikolay.stankinschedule.news.core.domain.model.NewsPost
import com.vereshchagin.nikolay.stankinschedule.core.ui.utils.newsImageLoader

@Preview(showBackground = true)
@Composable
fun NewsPostPreview() {
    AppTheme {
        NewsPost(
            post = NewsPost(
                0, "Example title.", "", "07.07.22",
            ),
            imageLoader = newsImageLoader(LocalContext.current),
            onClick = {},
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )
    }
}

@Composable
fun NewsPost(
    post: NewsPost?,
    imageLoader: ImageLoader,
    onClick: (post: NewsPost) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (post != null) {
        NewsPostContent(
            post = post,
            imageLoader = imageLoader,
            onClick = onClick,
            modifier = modifier
        )
    } else {
        NewsPostHolder(modifier = modifier)
    }
}

@Composable
private fun NewsPostHolder(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(Dimen.ContentPadding)
    ) {
        Box(
            modifier = Modifier
                .background(Color.Gray)
                .size(100.dp, 56.dp)
                .align(Alignment.CenterVertically)
        )
        Box(
            modifier = Modifier
                .background(Color.Gray)
                .weight(weight = 1f)
                .height(56.dp / 3)
        )
    }
}

@Composable
private fun NewsPostContent(
    post: NewsPost,
    imageLoader: ImageLoader,
    onClick: (post: NewsPost) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = Modifier
            .clickable(onClick = { onClick(post) })
            .then(modifier),
        horizontalArrangement = Arrangement.spacedBy(Dimen.ContentPadding)
    ) {
        AsyncImage(
            model = post.previewImageUrl,
            imageLoader = imageLoader,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(100.dp, 56.dp)
                .align(Alignment.CenterVertically)
        )
        Column(
            modifier = Modifier
                .weight(weight = 1f)
                .defaultMinSize(minHeight = 56.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = post.title,
                overflow = TextOverflow.Ellipsis,
                maxLines = 2
            )
            Text(
                text = formatDate(post.date),
                modifier = Modifier
                    .align(Alignment.End)
            )
        }
    }
}