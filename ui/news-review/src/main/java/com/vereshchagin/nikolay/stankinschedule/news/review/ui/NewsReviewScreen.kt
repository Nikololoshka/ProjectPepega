package com.vereshchagin.nikolay.stankinschedule.news.review.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.vereshchagin.nikolay.stankinschedule.core.ui.ext.Zero
import com.vereshchagin.nikolay.stankinschedule.news.core.domain.model.NewsSubdivision
import com.vereshchagin.nikolay.stankinschedule.core.ui.utils.newsImageLoader
import com.vereshchagin.nikolay.stankinschedule.news.review.ui.components.AppTabIndicator
import com.vereshchagin.nikolay.stankinschedule.news.review.ui.components.NewsPostColumn
import com.vereshchagin.nikolay.stankinschedule.news.review.ui.components.NewsReviewToolBar
import com.vereshchagin.nikolay.stankinschedule.news.review.ui.components.pagerTabIndicatorOffset
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class, ExperimentalMaterial3Api::class)
@Composable
fun NewsReviewScreen(
    viewModel: NewsReviewViewModel,
    navigateToViewer: (title: String?, newsId: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
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
            TabRow(
                selectedTabIndex = pagerState.currentPage,
                indicator = { tabPositions ->
                    AppTabIndicator(
                        modifier = Modifier.pagerTabIndicatorOffset(pagerState, tabPositions)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(tabRowHeight)
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

            HorizontalPager(
                count = newsSubdivisions.size,
                state = pagerState,
                key = { it },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = tabRowHeight)
            ) { page ->
                val subdivisionsId = newsSubdivisions[page].subdivisionsId
                val isRefreshing = viewModel.newsRefreshing(subdivisionsId).collectAsState()

                NewsPostColumn(
                    posts = viewModel.news(subdivisionsId),
                    onClick = { post -> navigateToViewer(post.title, post.id) },
                    isNewsRefreshing = isRefreshing.value,
                    onRefresh = { viewModel.refreshNews(subdivisionsId, force = true) },
                    imageLoader = imageLoader,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

private class NewsSubdivisionItem(
    @StringRes val nameId: Int,
    val subdivisionsId: Int,
)