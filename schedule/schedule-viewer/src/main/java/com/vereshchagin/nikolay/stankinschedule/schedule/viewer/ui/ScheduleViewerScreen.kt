package com.vereshchagin.nikolay.stankinschedule.schedule.viewer.ui

import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.vereshchagin.nikolay.stankinschedule.core.ui.components.CalendarDialog
import com.vereshchagin.nikolay.stankinschedule.core.utils.Zero
import com.vereshchagin.nikolay.stankinschedule.schedule.core.ui.PairColors
import com.vereshchagin.nikolay.stankinschedule.schedule.core.ui.ScheduleDayCard
import com.vereshchagin.nikolay.stankinschedule.schedule.viewer.R
import com.vereshchagin.nikolay.stankinschedule.schedule.viewer.ui.components.*
import dev.chrisbanes.snapper.ExperimentalSnapperApi
import dev.chrisbanes.snapper.SnapOffsets
import dev.chrisbanes.snapper.SnapperLayoutInfo
import dev.chrisbanes.snapper.rememberSnapperFlingBehavior

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSnapperApi::class,
    ExperimentalPermissionsApi::class
)
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

    val currentScheduleName by derivedStateOf {
        (scheduleState.scheduleName ?: scheduleName) ?: ""
    }

    val context = LocalContext.current
    val writeScheduleLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json"),
        onResult =  {
            if (it != null) {
                viewModel.saveToDevice(context, it)
            }
        }
    )

    val writePermission = rememberPermissionState(
        permission = android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        onPermissionResult = { isGrand ->
            if (isGrand) {
                writeScheduleLauncher.launch(currentScheduleName)
            }
        }
    )

    var isDaySelector by remember { mutableStateOf(false) }
    var isRemoveSchedule by remember { mutableStateOf(false) }
    val renameState by viewModel.renameState.collectAsState()

    Scaffold(
        topBar = {
            ScheduleViewerToolBar(
                scheduleName = currentScheduleName,
                onBackClicked = onBackPressed,
                onDayChangeClicked = { isDaySelector = true },
                onAddClicked = { onEditorClicked(scheduleId, null) },
                onRenameSchedule = { viewModel.onRenameEvent(RenameEvent.Rename) },
                onRemoveSchedule = { isRemoveSchedule = true },
                onSaveToDevice = {
                    // TODO(Доделать окна по показу доступа к записи)
                    writeScheduleLauncher.launch(currentScheduleName)

                    if (writePermission.status.isGranted || Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        writeScheduleLauncher.launch(currentScheduleName)
                    } else {
                        writePermission.launchPermissionRequest()
                    }
                }
            )
        },
        contentWindowInsets = WindowInsets.Zero,
        modifier = modifier
    ) { innerPadding ->

        if (isDaySelector) {
            CalendarDialog(
                selectedDate = viewModel.currentDay,
                onDateSelected = { viewModel.selectDate(it);isDaySelector = false },
                onDismissRequest = { isDaySelector = false }
            )
        }

        if (isRemoveSchedule) {
            ScheduleRemoveDialog(
                scheduleName = currentScheduleName,
                onRemove = { viewModel.removeSchedule(); isRemoveSchedule = false },
                onDismiss = { isRemoveSchedule = false }
            )
        }

        renameState?.let {
            ScheduleRenameDialog(
                currentScheduleName = currentScheduleName,
                state = it,
                onDismiss = { viewModel.onRenameEvent(RenameEvent.Cancel) },
                onRename = { newName -> viewModel.renameSchedule(newName) }
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

        if (scheduleState.isEmpty) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = stringResource(R.string.schedule_is_empty),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                TextButton(
                    onClick = { onEditorClicked(scheduleId, null) },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        text = stringResource(R.string.schedule_add_pair)
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