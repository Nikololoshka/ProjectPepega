package com.vereshchagin.nikolay.stankinschedule.home.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import com.vereshchagin.nikolay.stankinschedule.core.ui.components.Stateful
import com.vereshchagin.nikolay.stankinschedule.core.ui.components.TrackCurrentScreen
import com.vereshchagin.nikolay.stankinschedule.core.ui.ext.Zero
import com.vereshchagin.nikolay.stankinschedule.core.ui.theme.Dimen
import com.vereshchagin.nikolay.stankinschedule.core.ui.utils.BrowserUtils
import com.vereshchagin.nikolay.stankinschedule.core.ui.utils.newsImageLoader
import com.vereshchagin.nikolay.stankinschedule.home.ui.components.InAppUpdateDialog
import com.vereshchagin.nikolay.stankinschedule.home.ui.components.rememberInAppUpdater
import com.vereshchagin.nikolay.stankinschedule.home.ui.components.schedule.ScheduleHome
import com.vereshchagin.nikolay.stankinschedule.home.ui.data.UpdateState
import com.vereshchagin.nikolay.stankinschedule.news.core.domain.model.NewsPost
import com.vereshchagin.nikolay.stankinschedule.news.review.ui.components.NewsPost
import com.vereshchagin.nikolay.stankinschedule.schedule.core.ui.toColor
import com.vereshchagin.nikolay.stankinschedule.schedule.settings.domain.model.PairColorGroup

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    navigateToSchedule: (scheduleId: Long) -> Unit,
    navigateToNews: () -> Unit,
    navigateToNewsPost: (post: NewsPost) -> Unit,
    navigateToSettings: () -> Unit,
    modifier: Modifier = Modifier,
    imageLoader: ImageLoader = newsImageLoader(LocalContext.current)
) {
    TrackCurrentScreen(screen = "HomeScreen")

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(
        state = rememberTopAppBarState()
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.nav_home),
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                },
                actions = {
                    IconButton(
                        onClick = navigateToSettings
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_settings),
                            contentDescription = null
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        contentWindowInsets = WindowInsets.Zero,
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->

        val context = LocalContext.current
        val columnState = rememberLazyListState()

        val updateState = rememberInAppUpdater(
            saveLastUpdate = viewModel::saveLastUpdate,
            currentLastUpdate = viewModel::currentLastUpdate
        )
        LaunchedEffect(updateState.progress.value) {
            if (updateState.progress.value is UpdateState.UpdateRequired) {
                columnState.animateScrollToItem(0)
                scrollBehavior.state.contentOffset = 0f
            }
        }

        val favorite by viewModel.favorite.collectAsState()
        val scheduleDays by viewModel.days.collectAsState()
        val pairColorGroup by viewModel.pairColorGroup.collectAsState(PairColorGroup.default())
        val pairColors by remember(pairColorGroup) { derivedStateOf { pairColorGroup.toColor() } }

        val news by viewModel.news.collectAsState()

        LazyColumn(
            state = columnState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            item(key = "updater") {
                InAppUpdateDialog(
                    state = updateState,
                    modifier = Modifier
                        .fillParentMaxWidth()
                        .padding(Dimen.ContentPadding)
                )
            }

            item(key = "schedule_title") {
                HomeText(
                    text = favorite?.scheduleName
                        ?: stringResource(R.string.section_favorite_schedule),
                    modifier = Modifier
                        .fillParentMaxWidth()
                        .clickable {
                            val currentSchedule = favorite
                            if (currentSchedule != null) {
                                navigateToSchedule(currentSchedule.id)
                            }
                        }
                        .padding(Dimen.ContentPadding * 2)
                )
            }

            item(key = "schedule_pager") {
                Stateful(
                    state = scheduleDays,
                    onSuccess = { days ->
                        if (days.isNotEmpty()) {
                            ScheduleHome(
                                days = days,
                                onLinkClicked = { BrowserUtils.openLink(context, it) },
                                colors = pairColors,
                                modifier = Modifier
                                    .fillParentMaxWidth()
                                    .animateContentSize()
                            )
                        } else {
                            Text(
                                text = stringResource(R.string.favorite_not_selected),
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillParentMaxWidth()
                                    .padding(
                                        horizontal = Dimen.ContentPadding,
                                        vertical = Dimen.ContentPadding * 2
                                    )
                            )
                        }
                    },
                    onLoading = {
                        CircularProgressIndicator(
                            modifier = Modifier.padding(Dimen.ContentPadding * 2)
                        )
                    }
                )
            }

            item(key = "news_title") {
                HomeText(
                    text = stringResource(R.string.section_news),
                    modifier = Modifier
                        .fillParentMaxWidth()
                        .clickable(onClick = navigateToNews)
                        .padding(Dimen.ContentPadding * 2)
                )
            }

            items(
                count = HomeViewModel.NEWS_COUNT,
                key = { it }
            ) { index ->
                NewsPost(
                    post = news.getOrNull(index),
                    imageLoader = imageLoader,
                    onClick = {
                        navigateToNewsPost(it)
                    },
                    modifier = Modifier.padding(8.dp)
                )
                Divider()
            }

            item(key = "more_news") {
                Box(
                    modifier = Modifier.fillParentMaxWidth()
                ) {
                    TextButton(
                        onClick = navigateToNews,
                        modifier = Modifier.align(Alignment.Center)
                    ) {
                        Text(text = stringResource(R.string.more_news))
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeText(
    text: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
    ) {
        Text(
            text = text,
            style = TextStyle(fontSize = 18.sp),
            modifier = Modifier.weight(1f),
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
        Icon(
            painter = painterResource(R.drawable.ic_arrow_right),
            contentDescription = null
        )
    }
}
