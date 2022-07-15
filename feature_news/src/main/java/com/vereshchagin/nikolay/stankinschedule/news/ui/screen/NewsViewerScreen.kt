package com.vereshchagin.nikolay.stankinschedule.news.ui.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import com.vereshchagin.nikolay.stankinschedule.core.R
import com.vereshchagin.nikolay.stankinschedule.news.ui.components.NewsRenderer
import com.vereshchagin.nikolay.stankinschedule.news.util.State


@SuppressLint("SetJavaScriptEnabled")
@Composable
fun NewsViewerScreen(
    newsId: Int,
    viewModel: NewsViewerViewModel,
    modifier: Modifier = Modifier,
) {
    val newsContent = viewModel.newsContent.collectAsState()

    LaunchedEffect(newsId) {
        viewModel.loadNewsContent(newsId)
    }

    when (val content = newsContent.value) {
        is State.Success -> {
            NewsRenderer(
                content = content.data,
                onRedirect = {

                },
                modifier = modifier
                    .padding(dimensionResource(R.dimen.screen_padding))
            )
        }
        else -> {
            Box(
                modifier = modifier
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}