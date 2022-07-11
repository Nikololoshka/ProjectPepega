package com.vereshchagin.nikolay.stankinschedule.news.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import com.vereshchagin.nikolay.stankinschedule.news.domain.model.NewsSubdivision
import com.vereshchagin.nikolay.stankinschedule.news.ui.components.defaultImageLoader
import kotlinx.coroutines.launch


@OptIn(ExperimentalPagerApi::class)
@Composable
fun NewsReviewScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    val newsSubdivisions = listOf(
        NewsSubdivision("University", 0),
        NewsSubdivision("Decanat", 125)
    )

    val pagerState = rememberPagerState()
    val pagerScroller = rememberCoroutineScope()
    val imageLoader = defaultImageLoader(LocalContext.current)

    Column(
        modifier = modifier
    ) {
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    modifier = Modifier.pagerTabIndicatorOffset(pagerState, tabPositions)
                )
            }
        ) {
            newsSubdivisions.forEachIndexed { index, subdivision ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = {
                        pagerScroller.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    text = { Text(text = subdivision.name) }
                )
            }
        }
        HorizontalPager(
            count = newsSubdivisions.size,
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            NewsSubdivisionScreen(
                newsSubdivision = newsSubdivisions[page].id,
                navController = navController,
                imageLoader = imageLoader,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}