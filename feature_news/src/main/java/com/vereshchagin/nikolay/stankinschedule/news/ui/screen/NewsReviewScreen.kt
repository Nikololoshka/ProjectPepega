package com.vereshchagin.nikolay.stankinschedule.news.ui.screen

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Tab
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.vereshchagin.nikolay.stankinschedule.core.ui.components.AppTabIndicator
import com.vereshchagin.nikolay.stankinschedule.core.ui.components.AppTabRowInverted
import com.vereshchagin.nikolay.stankinschedule.core.ui.components.pagerTabIndicatorOffset
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

    Box(
        modifier = modifier
    ) {
        AppTabRowInverted(
            selectedTabIndex = pagerState.currentPage,
            indicator = { tabPositions ->
                AppTabIndicator(
                    modifier = Modifier.pagerTabIndicatorOffset(pagerState, tabPositions)
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
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
                            style = MaterialTheme.typography.body1
                        )
                    }
                )
            }
        }
        HorizontalPager(
            count = newsSubdivisions.size,
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 48.dp)
        ) { page ->
            val subdivisionsId = newsSubdivisions[page].subdivisionsId
            val isRefreshing = viewModel.newsRefreshing(subdivisionsId).collectAsState()

            NewsPostColumn(
                posts = viewModel.news(subdivisionsId),
                onClick = { post -> navigateToViewer(post.id) },
                isNewsRefreshing = isRefreshing.value,
                onRefresh = { viewModel.refreshNews(subdivisionsId) },
                modifier = Modifier.fillMaxSize(),
                imageLoader = imageLoader
            )
        }
    }
}

class NewsSubdivision(
    @StringRes val nameId: Int,
    val subdivisionsId: Int,
)