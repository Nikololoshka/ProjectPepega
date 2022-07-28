package com.vereshchagin.nikolay.stankinschedule.modulejournal.ui.screen

import android.content.Intent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.vereshchagin.nikolay.stankinschedule.core.ui.components.Stateful
import com.vereshchagin.nikolay.stankinschedule.modulejournal.ui.PredictActivity
import com.vereshchagin.nikolay.stankinschedule.modulejournal.ui.components.*


@OptIn(ExperimentalPagerApi::class, ExperimentalFoundationApi::class)
@Composable
fun JournalScreen(
    viewModel: JournalViewModel,
    navigateToLogging: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isSignIn by viewModel.isSignIn.collectAsState()
    LaunchedEffect(isSignIn) {
        if (!isSignIn) {
            navigateToLogging()
        }
    }

    val student by viewModel.student.collectAsState()
    val rating by viewModel.rating.collectAsState()
    val predictRating by viewModel.predictedRating.collectAsState()
    val forceRefreshing by viewModel.isForceRefreshing.collectAsState()

    val lazyCollapseState = rememberLazyListState()
    val pagerState = rememberPagerState()

    val density = LocalDensity.current
    var pagerHeight by remember { mutableStateOf(Dp.Unspecified) }
    val tabsHeight = 48.dp

    Stateful(
        state = student,
        onSuccess = { data ->
            SwipeRefresh(
                state = rememberSwipeRefreshState(isRefreshing = forceRefreshing),
                onRefresh = { viewModel.refreshStudentInfo(useCache = false) }
            ) {
                LazyColumn(
                    state = lazyCollapseState,
                    modifier = modifier
                        .onSizeChanged {
                            pagerHeight = with(density) { it.height.toDp() - tabsHeight }
                        }
                ) {

                    item {
                        StudentInfo(
                            student = data,
                            rating = rating,
                            predictRating = predictRating,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp)
                        )
                    }

                    stickyHeader {
                        SemesterTabRow(
                            semesters = data.semesters,
                            currentPage = pagerState.currentPage,
                            onPageScrolled = { index ->
                                pagerState.animateScrollToPage(index)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(tabsHeight)
                        )
                    }
                    item {
                        val context = LocalContext.current

                        Button(
                            onClick = {
                                context.startActivity(
                                    Intent(context, PredictActivity::class.java)
                                )
                            }
                        ) {
                            Text(text = "Predict")
                        }
                    }

                    item {
                        val semesters = viewModel.semesters.collectAsLazyPagingItems()
                        val semesterError: Throwable? = with(semesters.loadState) {
                            listOf(append, refresh, prepend)
                                .filterIsInstance(LoadState.Error::class.java)
                                .firstOrNull()?.error
                        }

                        HorizontalPager(
                            count = data.semesters.size,
                            state = pagerState,
                            verticalAlignment = Alignment.Top,
                            modifier = Modifier
                                .fillParentMaxWidth()
                                .defaultMinSize(minHeight = pagerHeight)
                        ) { page ->
                            val marks = semesters.getOrNull(page)

                            when {
                                marks == null && semesterError != null -> {
                                    JournalError(
                                        error = semesterError,
                                        onRetry = {
                                            semesters.retry()
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                                marks != null -> {
                                    MarksTable(
                                        semesterMarks = marks,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                                else -> {
                                    JournalLoading(
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        onLoading = {
            JournalLoading(
                modifier = modifier
            )
        },
        onFailed = { error ->
            JournalError(
                error = error,
                onRetry = {
                    viewModel.refreshStudentInfo(useCache = true)
                },
                modifier = modifier
            )
        }
    )
}

private fun <T : Any> LazyPagingItems<T>.getOrNull(index: Int): T? {
    return if (index in 0 until itemCount) get(index) else null
}