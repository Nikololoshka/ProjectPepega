package com.vereshchagin.nikolay.stankinschedule.modulejournal.ui.screen

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.vereshchagin.nikolay.stankinschedule.core.ui.State
import com.vereshchagin.nikolay.stankinschedule.modulejournal.domain.model.Student
import com.vereshchagin.nikolay.stankinschedule.modulejournal.ui.components.StudentInfo
import com.vereshchagin.nikolay.stankinschedule.modulejournal.ui.components.StudentInfoDefault
import kotlinx.coroutines.launch


@OptIn(ExperimentalPagerApi::class)
@Composable
fun JournalScreen(
    viewModel: JournalViewModel,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberLazyListState()

    val student = viewModel.student.collectAsState()

    val infoHeightFloat = with(LocalDensity.current) { 148.dp.toPx() }
    val collapseProgress by derivedStateOf {
        (1f - scrollState.firstVisibleItemScrollOffset / infoHeightFloat).coerceIn(0f, 1f)
    }
    val infoHeight by animateDpAsState(
        targetValue = StudentInfoDefault.TabsHeight + 148.dp * collapseProgress
    )

    val pagerState = rememberPagerState()
    val pagerScroller = rememberCoroutineScope()

    val nestedSCroll = rememberNestedScrollInteropConnection()

    Column(
        modifier = modifier
    ) {
        when (val currentStudent = student.value) {
            is State.Success -> {
                StudentInfo(
                    student = currentStudent.data,
                    collapseValue = collapseProgress,
                    selectedSemester = pagerState.currentPage,
                    onSemesterSelect = { index ->
                        pagerScroller.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(infoHeight),
                )

                HorizontalPager(
                    count = currentStudent.data.semesters.size,
                    state = pagerState,
                    modifier = Modifier
                        .weight(1f)
                        .nestedScroll(
                            object : NestedScrollConnection {

                            }
                        )
                ) { page ->
                    Column(
                        modifier = Modifier
                            .verticalScroll(rememberScrollState())
                            .fillMaxSize()
                    ) {
                        (0..100).forEach {
                            Text(
                                text = "Page: $page - $it",
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
            else -> {

            }
        }
    }
}