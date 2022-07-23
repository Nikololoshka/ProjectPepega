package com.vereshchagin.nikolay.stankinschedule.news.ui.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.vereshchagin.nikolay.stankinschedule.core.ui.BrowserUtils
import com.vereshchagin.nikolay.stankinschedule.core.ui.State
import com.vereshchagin.nikolay.stankinschedule.core.ui.components.Stateful
import com.vereshchagin.nikolay.stankinschedule.news.ui.components.NewsError
import com.vereshchagin.nikolay.stankinschedule.news.ui.components.NewsLoading
import com.vereshchagin.nikolay.stankinschedule.news.ui.components.NewsRenderer


@Composable
fun NewsViewerScreen(
    postId: Int,
    viewModel: NewsViewerViewModel,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val newsContent by viewModel.newsContent.collectAsState()
    val refreshState = rememberSwipeRefreshState(isRefreshing = newsContent is State.Loading)

    LaunchedEffect(postId) {
        viewModel.loadNewsContent(postId)
    }

    Stateful(
        state = newsContent,
        onSuccess = { content ->
            SwipeRefresh(
                state = refreshState,
                onRefresh = { viewModel.loadNewsContent(postId, force = true) },
                modifier = modifier
            ) {
                NewsRenderer(
                    content = content,
                    onRedirect = { uri -> BrowserUtils.openLink(context, uri) },
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                )
            }
        },
        onLoading = {
            NewsLoading(
                modifier = modifier
            )
        },
        onFailed = { error ->
            NewsError(
                error = error,
                onRetry = { viewModel.loadNewsContent(postId) },
                modifier = modifier
            )
        }
    )
}