package com.vereshchagin.nikolay.stankinschedule.schedule.viewer.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.vereshchagin.nikolay.stankinschedule.core.ui.components.CalendarDialog
import com.vereshchagin.nikolay.stankinschedule.core.utils.Zero
import com.vereshchagin.nikolay.stankinschedule.schedule.viewer.ui.components.PairColors
import com.vereshchagin.nikolay.stankinschedule.schedule.viewer.ui.components.ScheduleDayCard
import com.vereshchagin.nikolay.stankinschedule.schedule.viewer.ui.components.ScheduleState
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

    val scheduleState by viewModel.scheduleState.collectAsState()
    LaunchedEffect(scheduleState) {
        val state = scheduleState
        if (state is ScheduleState.NotFound) {
            onBackPressed()
        }
    }

    var isDaySelector by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            ScheduleViewerToolBar(
                scheduleName = (scheduleState.scheduleName ?: scheduleName) ?: "",
                onBackClicked = onBackPressed,
                onDayChangeClicked = { isDaySelector = true }
            )
        },
        contentWindowInsets = WindowInsets.Zero,
        modifier = modifier
    ) { innerPadding ->

        if (isDaySelector) {
            CalendarDialog(
                selectedDate = viewModel.currentDay,
                onDateSelected = {
                    viewModel.selectDate(it)
                    isDaySelector = false
                },
                onDismissRequest = { isDaySelector = false }
            )
        }

        val scheduleDays = viewModel.scheduleDays.collectAsLazyPagingItems()
        val scheduleListState = rememberLazyListState()
        val scheduleSnapper = rememberSnapperFlingBehavior(
            lazyListState = scheduleListState,
            snapOffsetForItem = SnapOffsets.Center,
            snapIndex = { info, start, target ->
                val index = computeScheduleIndex(info, start, target)
                viewModel.updatePagingDate(scheduleDays.peek(index)?.day)
                index
            }
        )

        val colors = PairColors.defaults()

        if (scheduleState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }

        if (scheduleState.isEmpty) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = "Schedule is empty",
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }

        LazyRow(
            state = scheduleListState,
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
                            onEditorClicked(scheduleId, pair.id)
                        },
                        modifier = Modifier.fillParentMaxSize()
                    )
                }
            }
        }
    }
}

private val ScheduleState.scheduleName: String?
    get() =
        if (this is ScheduleState.Success) this.scheduleName else null

private val ScheduleState.isLoading
    get() =
        this !is ScheduleState.Success

private val ScheduleState.isEmpty
    get() =
        if (this is ScheduleState.Success) this.isEmpty else false

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