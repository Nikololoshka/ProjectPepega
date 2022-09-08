package com.vereshchagin.nikolay.stankinschedule.news.review.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.vereshchagin.nikolay.stankinschedule.news.review.R

@Composable
fun NewsReviewToolBar(
    modifier: Modifier = Modifier,
    title: String = stringResource(R.string.news_review_title),
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    SmallTopAppBar(
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