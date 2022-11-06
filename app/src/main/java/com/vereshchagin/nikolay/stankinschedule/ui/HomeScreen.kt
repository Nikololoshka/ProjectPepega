package com.vereshchagin.nikolay.stankinschedule.ui

import android.content.Intent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import com.vereshchagin.nikolay.stankinschedule.R
import com.vereshchagin.nikolay.stankinschedule.core.ui.components.Stateful
import com.vereshchagin.nikolay.stankinschedule.core.ui.getOrNull
import com.vereshchagin.nikolay.stankinschedule.core.ui.theme.Dimen
import com.vereshchagin.nikolay.stankinschedule.core.utils.Zero
import com.vereshchagin.nikolay.stankinschedule.news.core.utils.newsImageLoader
import com.vereshchagin.nikolay.stankinschedule.news.review.ui.components.NewsPost
import com.vereshchagin.nikolay.stankinschedule.schedule.core.ui.data.toColor
import com.vereshchagin.nikolay.stankinschedule.schedule.home.ui.ScheduleHome
import com.vereshchagin.nikolay.stankinschedule.schedule.settings.domain.model.PairColorGroup
import com.vereshchagin.nikolay.stankinschedule.settings.ui.SettingsActivity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    navigateToSchedule: (scheduleId: Long) -> Unit,
    navigateToNews: () -> Unit,
    navigateToNewsPost: (title: String, newsId: Int) -> Unit,
    modifier: Modifier = Modifier,
    imageLoader: ImageLoader = newsImageLoader(LocalContext.current)
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(
        state = rememberTopAppBarState()
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.nav_home)) },
                actions = {
                    val context = LocalContext.current
                    IconButton(
                        onClick = {
                            context.startActivity(Intent(context, SettingsActivity::class.java))
                        }
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

        val scheduleHome by viewModel.days.collectAsState()
        val pairColorGroup by viewModel.pairColorGroup.collectAsState(PairColorGroup.default())
        val pairColors by remember(pairColorGroup) { derivedStateOf { pairColorGroup.toColor() } }

        val news by viewModel.news.collectAsState()

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            item {
                HomeText(
                    text = scheduleHome.getOrNull()?.scheduleName
                        ?: stringResource(R.string.section_favorite_schedule),
                    modifier = Modifier
                        .fillParentMaxWidth()
                        .clickable {
                            val info = scheduleHome.getOrNull()
                            if (info != null) {
                                navigateToSchedule(info.scheduleId)
                            }
                        }
                        .padding(Dimen.ContentPadding * 2)
                )
            }

            item {
                Stateful(
                    state = scheduleHome,
                    onSuccess = {
                        if (it != null) {
                            ScheduleHome(
                                days = it.days,
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

            item {
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
                        navigateToNewsPost(it.title, it.id)
                    },
                    modifier = Modifier.padding(8.dp)
                )
                Divider()
            }

            item {
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
            modifier = Modifier.weight(1f)
        )
        Icon(
            painter = painterResource(R.drawable.ic_arrow_right),
            contentDescription = null
        )
    }
}
