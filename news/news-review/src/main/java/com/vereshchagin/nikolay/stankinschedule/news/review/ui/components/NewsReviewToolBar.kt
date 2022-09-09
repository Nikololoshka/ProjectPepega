package com.vereshchagin.nikolay.stankinschedule.news.review.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.vereshchagin.nikolay.stankinschedule.news.review.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsReviewToolBar(
    modifier: Modifier = Modifier,
    title: String = stringResource(R.string.news_review_title),
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge
            )
        },
        scrollBehavior = scrollBehavior,
        modifier = modifier
    )
}