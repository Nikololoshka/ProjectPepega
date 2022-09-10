package com.vereshchagin.nikolay.stankinschedule.schedule.viewer.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.vereshchagin.nikolay.stankinschedule.core.ui.components.CalendarDialog
import com.vereshchagin.nikolay.stankinschedule.core.utils.Zero
import com.vereshchagin.nikolay.stankinschedule.schedule.viewer.ui.components.PairColors
import com.vereshchagin.nikolay.stankinschedule.schedule.viewer.ui.components.ScheduleDayCard
import com.vereshchagin.nikolay.stankinschedule.schedule.viewer.ui.components.ScheduleViewerToolBar
import dev.chrisbanes.snapper.ExperimentalSnapperApi
import dev.chrisbanes.snapper.SnapOffsets
import dev.chrisbanes.snapper.SnapperLayoutInfo
import dev.chrisbanes.snapper.rememberSnapperFlingBehavior

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSnapperApi::class)
@Composable
fun ScheduleViewerScreen(
    scheduleId: Long,
    scheduleName: String?,
    viewModel: ScheduleViewerViewModel,
    onBackPressed: () -> Unit,
    onEditorClicked: (scheduleId: Long, pairId: Long?) -> Unit,
    modifier: Modifier = Modifier,
) {
    LaunchedEffect(scheduleId) {
        viewModel.loadSchedule(scheduleId)
    }

    val scheduleInfo by viewModel.scheduleInfo.collectAsState()
    var isDaySelector by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            ScheduleViewerToolBar(
                scheduleName = scheduleInfo?.scheduleName ?: scheduleName ?: "",
                onBackClicked = onBackPressed,
                onDayChangeClicked = { isDaySelector = true }
            )
        },
        contentWindowInsets = WindowInsets.Zero,
        modifier = modifier
    ) { innerPadding ->

        if (isDaySelector) {
            CalendarDialog(
                onDateSelected = {
                    viewModel.selectDate(it)
                    isDaySelector = false
                },
                onDismissRequest = { isDaySelector = false }
            )
        }

        val scheduleDays = viewModel.scheduleDays.collectAsLazyPagingItems()
        val scheduleState = rememberLazyListState()
        val scheduleSnapper = rememberSnapperFlingBehavior(
            lazyListState = scheduleState,
            snapOffsetForItem = SnapOffsets.Center,
            snapIndex = { info, start, target ->
                val index = computeScheduleIndex(info, start, target)
                viewModel.updatePagingDate(scheduleDays.peek(index)?.day)
                index
            }
        )

        val colors = PairColors(
            Color.Blue,
            Color.Yellow,
            Color.Green,
            Color.Magenta,
            Color.DarkGray
        )

        LazyRow(
            state = scheduleState,
            flingBehavior = scheduleSnapper,
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
                        onPairClicked = { pair ->
                            onEditorClicked(scheduleId, pair.info.id)
                        },
                        modifier = Modifier.fillParentMaxSize()
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalSnapperApi::class)
private fun computeScheduleIndex(
    info: SnapperLayoutInfo,
    start: Int,
    target: Int,
    delta: Int = 120,
): Int {
    if (start == target) {
        val distance = info.distanceToIndexSnap(target)

        when {
            distance < -delta -> {
                return target + 1
            }
            distance > delta -> {
                return target - 1
            }
        }
    }
    return target
}