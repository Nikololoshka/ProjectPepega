package com.vereshchagin.nikolay.stankinschedule.ui

import android.content.Intent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import com.vereshchagin.nikolay.stankinschedule.R
import com.vereshchagin.nikolay.stankinschedule.core.ui.theme.Dimen
import com.vereshchagin.nikolay.stankinschedule.core.utils.Zero
import com.vereshchagin.nikolay.stankinschedule.news.core.utils.newsImageLoader
import com.vereshchagin.nikolay.stankinschedule.news.review.ui.components.NewsPost
import com.vereshchagin.nikolay.stankinschedule.schedule.home.ui.ScheduleHome
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
        val news by viewModel.news.collectAsState()

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            item {
                HomeText(
                    text = scheduleHome?.scheduleName
                        ?: stringResource(R.string.section_favorite_schedule),
                    modifier = Modifier
                        .fillParentMaxWidth()
                        .clickable {
                            val info = scheduleHome
                            if (info != null) {
                                navigateToSchedule(info.scheduleId)
                            }
                        }
                        .padding(Dimen.ContentPadding * 2)
                )
            }

            item {
                scheduleHome?.let {
                    ScheduleHome(
                        days = it.days,
                        modifier = Modifier
                            .fillParentMaxWidth()
                            .animateContentSize()
                    )
                }
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

            items(news, key = { it.id }) { post ->
                NewsPost(
                    post = post,
                    imageLoader = imageLoader,
                    onClick = {
                        navigateToNewsPost(post.title, post.id)
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
