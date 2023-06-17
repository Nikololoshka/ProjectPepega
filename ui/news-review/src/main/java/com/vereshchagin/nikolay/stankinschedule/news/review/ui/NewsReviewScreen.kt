package com.vereshchagin.nikolay.stankinschedule.news.review.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.vereshchagin.nikolay.stankinschedule.core.ui.components.TrackCurrentScreen
import com.vereshchagin.nikolay.stankinschedule.core.ui.ext.Zero
import com.vereshchagin.nikolay.stankinschedule.core.ui.utils.newsImageLoader
import com.vereshchagin.nikolay.stankinschedule.news.core.domain.model.NewsSubdivision
import com.vereshchagin.nikolay.stankinschedule.news.review.ui.components.AppTabIndicator
import com.vereshchagin.nikolay.stankinschedule.news.review.ui.components.NewsPostColumn
import com.vereshchagin.nikolay.stankinschedule.news.review.ui.components.NewsReviewToolBar
import com.vereshchagin.nikolay.stankinschedule.news.review.ui.components.pagerTabIndicatorOffset
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun NewsReviewScreen(
    viewModel: NewsReviewViewModel,
    navigateToViewer: (title: String?, newsId: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    TrackCurrentScreen(screen = "NewsReviewScreen")

    val newsSubdivisions = listOf(
        NewsSubdivisionItem(
            nameId = R.string.news_university,
            subdivisionsId = NewsSubdivision.University.id
        ),
        NewsSubdivisionItem(
            nameId = R.string.news_deanery,
            subdivisionsId = NewsSubdivision.Deanery.id
        )
    )

    val pagerState = rememberPagerState()
    val pagerScroller = rememberCoroutineScope()
    val imageLoader = newsImageLoader(LocalContext.current)
    val tabRowHeight = 48.dp

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(
        state = rememberTopAppBarState()
    )

    Scaffold(
        topBar = {
            NewsReviewToolBar(
                scrollBehavior = scrollBehavior
            )
        },
        contentWindowInsets = WindowInsets.Zero,
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        Box(
            modifier = Modifier.padding(innerPadding)
        ) {
            androidx.compose.foundation.pager.HorizontalPager(
                pageCount = newsSubdivisions.size,
                state = pagerState,
                key = { it },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = tabRowHeight)
            ) { page ->
                val subdivisionsId = newsSubdivisions[page].subdivisionsId
                val isRefreshing = viewModel.newsRefreshing(subdivisionsId).collectAsState()
                val columnState = rememberLazyListState()

                NewsPostColumn(
                    posts = viewModel.news(subdivisionsId),
                    onClick = { post -> navigateToViewer(post.title, post.id) },
                    isNewsRefreshing = isRefreshing.value,
                    onRefresh = { viewModel.refreshNews(subdivisionsId, force = true) },
                    imageLoader = imageLoader,
                    columnState = columnState,
                    modifier = Modifier.fillMaxSize()
                )
            }

            TabRow(
                selectedTabIndex = pagerState.currentPage,
                indicator = { tabPositions ->
                    AppTabIndicator(
                        modifier = Modifier.pagerTabIndicatorOffset(pagerState, tabPositions)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = tabRowHeight)
            ) {
                newsSubdivisions.forEachIndexed { index, subdivision ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            pagerScroller.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        text = {
                            Text(text = stringResource(id = subdivision.nameId))
                        }
                    )
                }
            }
        }
    }
}

private class NewsSubdivisionItem(
    @StringRes val nameId: Int,
    val subdivisionsId: Int,
)