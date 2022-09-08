package com.vereshchagin.nikolay.stankinschedule.news.review.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.AsyncImage
import com.vereshchagin.nikolay.stankinschedule.core.ui.formatDate
import com.vereshchagin.nikolay.stankinschedule.core.ui.theme.AppTheme
import com.vereshchagin.nikolay.stankinschedule.news.core.domain.model.NewsPost
import com.vereshchagin.nikolay.stankinschedule.news.core.utils.newsImageLoader

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
    post: NewsPost,
    imageLoader: ImageLoader,
    onClick: (post: NewsPost) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = Modifier
            .clickable(onClick = { onClick(post) })
            .then(modifier),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
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
                .defaultMinSize(minHeight = 60.dp),
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