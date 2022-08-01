package com.vereshchagin.nikolay.stankinschedule.news.ui.viewer

import android.content.Intent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.vereshchagin.nikolay.stankinschedule.core.ui.BrowserUtils
import com.vereshchagin.nikolay.stankinschedule.core.ui.State
import com.vereshchagin.nikolay.stankinschedule.core.ui.components.Stateful
import com.vereshchagin.nikolay.stankinschedule.news.ui.components.NewsError
import com.vereshchagin.nikolay.stankinschedule.news.ui.components.NewsLoading
import com.vereshchagin.nikolay.stankinschedule.news.ui.viewer.components.NewsBrowserUtils
import com.vereshchagin.nikolay.stankinschedule.news.ui.viewer.components.NewsRenderer
import com.vereshchagin.nikolay.stankinschedule.news.ui.viewer.components.NewsViewerToolBar


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsViewerScreen(
    title: String?,
    postId: Int,
    viewModel: NewsViewerViewModel,
    onBackPressed: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val newsContent by viewModel.newsContent.collectAsState()
    val refreshState = rememberSwipeRefreshState(isRefreshing = newsContent is State.Loading)

    LaunchedEffect(postId) {
        viewModel.loadNewsContent(postId)
    }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        state = rememberTopAppBarScrollState()
    )

    Scaffold(
        topBar = {
            NewsViewerToolBar(
                title = title,
                onBackPressed = onBackPressed,
                onOpenInBrowser = {
                    BrowserUtils.openLink(context, NewsBrowserUtils.linkForPost(postId))
                },
                onShareNews = {
                    val url = NewsBrowserUtils.linkForPost(postId)
                    val sendIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, url)
                        type = "text/plain"
                    }
                    val shareIntent = Intent.createChooser(sendIntent, null)
                    context.startActivity(shareIntent)
                },
                onUpdateNews = { viewModel.loadNewsContent(postId, force = true) },
                scrollBehavior = scrollBehavior
            )
        },
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        Stateful(
            state = newsContent,
            onSuccess = { content ->
                SwipeRefresh(
                    state = refreshState,
                    onRefresh = { viewModel.loadNewsContent(postId, force = true) },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
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
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            },
            onFailed = { error ->
                NewsError(
                    error = error,
                    onRetry = { viewModel.loadNewsContent(postId) },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }
        )
    }


}