package com.vereshchagin.nikolay.stankinschedule.modulejournal.ui.screen

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.vereshchagin.nikolay.stankinschedule.core.ui.State
import com.vereshchagin.nikolay.stankinschedule.core.ui.components.CollapseLayout
import com.vereshchagin.nikolay.stankinschedule.modulejournal.ui.components.MarksTable
import com.vereshchagin.nikolay.stankinschedule.modulejournal.ui.components.SemesterTabRow
import com.vereshchagin.nikolay.stankinschedule.modulejournal.ui.components.StudentInfo


@OptIn(ExperimentalPagerApi::class)
@Composable
fun JournalScreen(
    viewModel: JournalViewModel,
    modifier: Modifier = Modifier,
) {
    val student = viewModel.student.collectAsState()

    val pagerState = rememberPagerState()

    when (val currentStudent = student.value) {
        is State.Success -> {
            CollapseLayout(
                headerHeight = 150.dp,
                header = { progress ->
                    StudentInfo(
                        student = currentStudent.data,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp * (1 - progress))
                            .alpha(alpha = (1 - progress))
                            .defaultMinSize(minHeight = 150.dp)
                    )
                },
                content = {
                    Column {
                        SemesterTabRow(
                            semesters = currentStudent.data.semesters,
                            currentPage = pagerState.currentPage,
                            onPageScrolled = { index ->
                                pagerState.animateScrollToPage(index)
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                        HorizontalPager(
                            count = currentStudent.data.semesters.size,
                            state = pagerState,
                            verticalAlignment = Alignment.Top,
                            modifier = Modifier
                                .weight(1f)
                                .verticalScroll(rememberScrollState())
                                .animateContentSize()
                        ) { page ->
                            val semester = currentStudent.data.semesters[page]
                            val marks by viewModel.semesterMarks(semester).collectAsState()

                            if (marks is State.Success) {
                                MarksTable(
                                    semesterMarks = (marks as State.Success).data,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                },
                modifier = modifier
            )
        }
        else -> {

        }
    }

}
