package com.vereshchagin.nikolay.stankinschedule.schedule.viewer.ui

import android.content.Intent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.vereshchagin.nikolay.stankinschedule.schedule.viewer.ui.components.PairColors
import com.vereshchagin.nikolay.stankinschedule.schedule.viewer.ui.components.ScheduleDayCard
import com.vereshchagin.nikolay.stankinschedule.schedule.viewer.ui.components.ScheduleViewerToolBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleViewerScreen(
    scheduleId: Long,
    scheduleName: String?,
    viewModel: ScheduleViewerViewModel,
    onBackPressed: () -> Unit,
    onEditorClicked: (id: Int?) -> Unit,
    modifier: Modifier = Modifier,
) {
    LaunchedEffect(scheduleId) {
        viewModel.loadSchedule(scheduleId)
    }

    val context = LocalContext.current
    val scheduleInfo by viewModel.scheduleInfo.collectAsState()


    Scaffold(
        topBar = {
            ScheduleViewerToolBar(
                scheduleName = scheduleInfo?.scheduleName ?: scheduleName ?: "",
                onBackClicked = onBackPressed
            )
        },
        modifier = modifier
    ) { innerPadding ->

        val scheduleDays = viewModel.scheduleDays.collectAsLazyPagingItems()
        val scheduleState = rememberLazyListState()
        // val scheduleSnapper = rememberSnapperFlingBehavior(lazyListState = scheduleState)

        val colors = PairColors(
            Color.Blue,
            Color.Yellow,
            Color.Green,
            Color.Magenta,
            Color.DarkGray
        )

        LazyRow(
            state = scheduleState,
            // flingBehavior = scheduleSnapper,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .animateContentSize()
        ) {
            items(scheduleDays, key = { it.day }) { day ->
                if (day != null) {
                    ScheduleDayCard(
                        scheduleDay = day,
                        pairColors = colors,
                        onPairClicked = {
                                        onEditorClicked(null)
                        },
                        modifier = Modifier.fillParentMaxSize()
                    )
                }
            }
        }
    }
}