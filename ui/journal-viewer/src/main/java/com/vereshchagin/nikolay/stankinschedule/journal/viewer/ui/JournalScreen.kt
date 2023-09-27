package com.vereshchagin.nikolay.stankinschedule.journal.viewer.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.vereshchagin.nikolay.stankinschedule.core.ui.components.NotificationUpdateDialog
import com.vereshchagin.nikolay.stankinschedule.core.ui.components.Stateful
import com.vereshchagin.nikolay.stankinschedule.core.ui.components.TrackCurrentScreen
import com.vereshchagin.nikolay.stankinschedule.core.ui.components.isSuccess
import com.vereshchagin.nikolay.stankinschedule.core.ui.components.rememberNotificationUpdateState
import com.vereshchagin.nikolay.stankinschedule.core.ui.ext.Zero
import com.vereshchagin.nikolay.stankinschedule.core.ui.theme.Dimen
import com.vereshchagin.nikolay.stankinschedule.journal.viewer.ui.components.JournalError
import com.vereshchagin.nikolay.stankinschedule.journal.viewer.ui.components.JournalLoading
import com.vereshchagin.nikolay.stankinschedule.journal.viewer.ui.components.JournalToolBar
import com.vereshchagin.nikolay.stankinschedule.journal.viewer.ui.components.MarksTable
import com.vereshchagin.nikolay.stankinschedule.journal.viewer.ui.components.SemesterTabRow
import com.vereshchagin.nikolay.stankinschedule.journal.viewer.ui.components.StudentInfo
import com.vereshchagin.nikolay.stankinschedule.journal.viewer.ui.worker.JournalMarksUpdateWorker


@OptIn(
    ExperimentalFoundationApi::class,
    ExperimentalMaterial3Api::class,
    ExperimentalMaterialApi::class
)
@Composable
fun JournalScreen(
    viewModel: JournalViewModel,
    navigateToLogging: () -> Unit,
    navigateToPredict: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TrackCurrentScreen(screen = "JournalScreen")

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

    val semesters = viewModel.semesters.collectAsLazyPagingItems()
    val semesterError: Throwable? = with(semesters.loadState) {
        listOf(append, refresh, prepend)
            .filterIsInstance(LoadState.Error::class.java)
            .firstOrNull()?.error
    }

    val lazyCollapseState = rememberLazyListState()
    val pagerState = rememberPagerState(
        pageCount = { semesters.itemCount }
    )

    val density = LocalDensity.current
    var pagerHeight by remember { mutableStateOf(Dp.Unspecified) }
    val tabsHeight = 48.dp

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(
        state = rememberTopAppBarState()
    )

    val context = LocalContext.current
    val isNotification by viewModel.isNotification.collectAsState(false)
    LaunchedEffect(isNotification) {
        if (isNotification) {
            JournalMarksUpdateWorker.startWorker(context)
        } else {
            JournalMarksUpdateWorker.cancelWorker(context)
        }
    }
    val notificationState = rememberNotificationUpdateState(
        isEnabled = isNotification,
        onChanged = viewModel::setUpdateMarksAllow
    )

    Scaffold(
        topBar = {
            JournalToolBar(
                onPredictAction = { if (student.isSuccess()) navigateToPredict() },
                isNotification = isNotification,
                onNotificationAction = notificationState::showDialog,
                onSignOutAction = viewModel::signOut,
                scrollBehavior = scrollBehavior,
            )
        },
        contentWindowInsets = WindowInsets.Zero,
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        val refreshState = rememberPullRefreshState(
            refreshing = forceRefreshing,
            onRefresh = { viewModel.refreshStudentInfo(useCache = false) }
        )

        Stateful(
            state = student,
            onSuccess = { data ->
                Box(
                    modifier = Modifier
                        .pullRefresh(refreshState)
                        .padding(innerPadding)
                ) {
                    NotificationUpdateDialog(
                        title = stringResource(R.string.notification_journal_title),
                        content = stringResource(R.string.notification_journal_text),
                        state = notificationState
                    )

                    LazyColumn(
                        state = lazyCollapseState,
                        modifier = modifier
                            .onSizeChanged {
                                pagerHeight = with(density) { it.height.toDp() - tabsHeight }
                            }
                    ) {

                        item(key = "student_info") {
                            StudentInfo(
                                student = data,
                                rating = rating,
                                predictRating = predictRating,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(Dimen.ContentPadding)
                                    .defaultMinSize(minHeight = 140.dp)
                            )
                        }

                        stickyHeader(key = "semesters") {
                            SemesterTabRow(
                                semesters = data.semesters,
                                currentPage = pagerState.currentPage,
                                onPageScrolled = { index ->
                                    if (index >= 0 && index < pagerState.pageCount) {
                                        pagerState.animateScrollToPage(index)
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .defaultMinSize(minHeight = tabsHeight)
                            )
                        }

                        item(key = "marks") {
                            HorizontalPager(
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
                                            textColor = MaterialTheme.colorScheme.onSurface.toArgb(),
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

                    PullRefreshIndicator(
                        refreshing = forceRefreshing,
                        state = refreshState,
                        modifier = Modifier.align(Alignment.TopCenter)
                    )
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
}

private fun <T : Any> LazyPagingItems<T>.getOrNull(index: Int): T? {
    return if (index in 0 until itemCount) get(index) else null
}