package com.vereshchagin.nikolay.stankinschedule.news.ui.screen

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import com.vereshchagin.nikolay.stankinschedule.core.ui.components.AppTabIndicator
import com.vereshchagin.nikolay.stankinschedule.news.R
import com.vereshchagin.nikolay.stankinschedule.news.ui.components.NewsPostColumn
import com.vereshchagin.nikolay.stankinschedule.news.utils.newsImageLoader
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
@Composable
fun NewsReviewScreen(
    viewModel: NewsReviewViewModel,
    navigateToViewer: (newsId: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val newsSubdivisions = listOf(
        NewsSubdivision(nameId = R.string.news_university, subdivisionsId = 0),
        NewsSubdivision(nameId = R.string.news_deanery, subdivisionsId = 125)
    )

    val pagerState = rememberPagerState()
    val pagerScroller = rememberCoroutineScope()
    val imageLoader = newsImageLoader(LocalContext.current)

    Column(
        modifier = modifier
    ) {
        val indicator = @Composable { tabPositions: List<TabPosition> ->
            AppTabIndicator(
                modifier = Modifier.pagerTabIndicatorOffset(pagerState, tabPositions)
            )
        }

        TabRow(
            selectedTabIndex = pagerState.currentPage,
            indicator = indicator
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
                        Text(
                            text = stringResource(id = subdivision.nameId),
                            style = MaterialTheme.typography.body2
                        )
                    }
                )
            }
        }
        HorizontalPager(
            count = newsSubdivisions.size,
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val subdivisionsId = newsSubdivisions[page].subdivisionsId
            val isRefreshing = viewModel.newsRefreshing(subdivisionsId).collectAsState()

            NewsPostColumn(
                posts = viewModel.news(subdivisionsId),
                onClick = { post -> navigateToViewer(post.id) },
                isNewsRefreshing = isRefreshing.value,
                onRefresh = { viewModel.refreshNews(subdivisionsId) },
                imageLoader = imageLoader,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

class NewsSubdivision(
    @StringRes val nameId: Int,
    val subdivisionsId: Int,
)