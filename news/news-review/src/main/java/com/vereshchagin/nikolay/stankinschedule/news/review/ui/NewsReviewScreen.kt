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
import com.vereshchagin.nikolay.stankinschedule.core.ui.components.AppTabIndicator
import com.vereshchagin.nikolay.stankinschedule.core.ui.components.pagerTabIndicatorOffset
import com.vereshchagin.nikolay.stankinschedule.core.utils.Zero
import com.vereshchagin.nikolay.stankinschedule.news.core.utils.newsImageLoader
import com.vereshchagin.nikolay.stankinschedule.news.review.R
import com.vereshchagin.nikolay.stankinschedule.news.review.ui.components.NewsPostColumn
import com.vereshchagin.nikolay.stankinschedule.news.review.ui.components.NewsReviewToolBar
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class, ExperimentalMaterial3Api::class)
@Composable
fun NewsReviewScreen(
    viewModel: NewsReviewViewModel,
    navigateToViewer: (title: String?, newsId: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val newsSubdivisions = listOf(
        NewsSubdivision(nameId = R.string.news_university, subdivisionsId = 0),
        NewsSubdivision(nameId = R.string.news_deanery, subdivisionsId = 125)
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
                    onRefresh = { viewModel.refreshNews(subdivisionsId) },
                    modifier = Modifier.fillMaxSize(),
                    imageLoader = imageLoader
                )
            }
        }
    }
}

class NewsSubdivision(
    @StringRes val nameId: Int,
    val subdivisionsId: Int,
)