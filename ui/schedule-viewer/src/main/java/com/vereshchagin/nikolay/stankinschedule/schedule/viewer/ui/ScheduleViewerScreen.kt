package com.vereshchagin.nikolay.stankinschedule.schedule.viewer.ui

import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.vereshchagin.nikolay.stankinschedule.core.ui.components.CalendarDialog
import com.vereshchagin.nikolay.stankinschedule.core.ui.components.FileSaveDialogs
import com.vereshchagin.nikolay.stankinschedule.core.ui.components.TrackCurrentScreen
import com.vereshchagin.nikolay.stankinschedule.core.ui.components.rememberFileSaveState
import com.vereshchagin.nikolay.stankinschedule.core.ui.ext.Zero
import com.vereshchagin.nikolay.stankinschedule.core.ui.utils.BrowserUtils
import com.vereshchagin.nikolay.stankinschedule.schedule.core.ui.ScheduleDayCard
import com.vereshchagin.nikolay.stankinschedule.schedule.core.ui.toColor
import com.vereshchagin.nikolay.stankinschedule.schedule.settings.domain.model.PairColorGroup
import com.vereshchagin.nikolay.stankinschedule.schedule.viewer.ui.components.ExportProgress
import com.vereshchagin.nikolay.stankinschedule.schedule.viewer.ui.components.RenameEvent
import com.vereshchagin.nikolay.stankinschedule.schedule.viewer.ui.components.SaveFormatDialog
import com.vereshchagin.nikolay.stankinschedule.schedule.viewer.ui.components.ScheduleRemoveDialog
import com.vereshchagin.nikolay.stankinschedule.schedule.viewer.ui.components.ScheduleRenameDialog
import com.vereshchagin.nikolay.stankinschedule.schedule.viewer.ui.components.ScheduleState
import com.vereshchagin.nikolay.stankinschedule.schedule.viewer.ui.components.ScheduleViewerToolBar
import com.vereshchagin.nikolay.stankinschedule.schedule.viewer.ui.components.rememberSaveFormatDialogState
import org.joda.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleViewerScreen(
    scheduleId: Long,
    startDate: String?,
    scheduleName: String?,
    viewModel: ScheduleViewerViewModel,
    onBackPressed: () -> Unit,
    onEditorClicked: (scheduleId: Long, pairId: Long?) -> Unit,
    onTableViewClicked: (scheduleId: Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    TrackCurrentScreen(screen = "ScheduleViewerScreen")

    LaunchedEffect(scheduleId) {
        viewModel.loadSchedule(scheduleId, startDate?.let { LocalDate.parse(it) })
    }

    val scheduleState by viewModel.scheduleState.collectAsState()
    LaunchedEffect(scheduleState) {
        val state = scheduleState
        if (state is ScheduleState.NotFound) {
            onBackPressed()
        }
    }

    val currentScheduleName by remember(scheduleState.scheduleName) {
        derivedStateOf { (scheduleState.scheduleName ?: scheduleName) ?: "" }
    }

    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    val saveFileState = rememberFileSaveState(
        onPickerResult = {
            if (it != null) {
                viewModel.saveAs(it)
            }
        }
    )

    val saveFormatState = rememberSaveFormatDialogState(
        onFormatSelected = {
            viewModel.setSaveFormat(it)
            saveFileState.save(currentScheduleName, it.memeType)
        }
    )

    val snackBarHostState = remember { SnackbarHostState() }
    val successfullySaved = stringResource(R.string.successfully_saved)
    val openAction = stringResource(R.string.open)
    val saveProgress by viewModel.saveProgress.collectAsState()

    LaunchedEffect(saveProgress) {
        when (val progress = saveProgress) {
            is ExportProgress.Finished -> {
                val snackResult = snackBarHostState.showSnackbar(
                    message = successfullySaved,
                    actionLabel = openAction,
                    duration = SnackbarDuration.Long
                )

                if (snackResult == SnackbarResult.ActionPerformed) {
                    val intent = Intent().apply {
                        action = Intent.ACTION_VIEW
                        setDataAndType(progress.path, progress.format.memeType)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    context.startActivity(Intent.createChooser(intent, null))
                }

                viewModel.saveFinished()
            }

            is ExportProgress.Error -> {
                snackBarHostState.showSnackbar(
                    message = progress.error.toString(),
                    duration = SnackbarDuration.Short
                )
                viewModel.saveFinished()
            }

            else -> {}
        }
    }

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
                onSaveToDevice = saveFormatState::showDialog
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onTableViewClicked(scheduleId) }
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_table_chart),
                    contentDescription = null
                )
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        contentWindowInsets = WindowInsets.Zero,
        modifier = modifier
    ) { innerPadding ->

        FileSaveDialogs(state = saveFileState)

        SaveFormatDialog(state = saveFormatState)

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

        // TODO("Изменение имени расписания на виджете")
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

        val isVerticalViewer by viewModel.isVerticalViewer.collectAsState(false)
        val pairColorGroup by viewModel.pairColorGroup.collectAsState(PairColorGroup.default())
        val pairColors by remember(pairColorGroup) { derivedStateOf { pairColorGroup.toColor() } }

        if (scheduleState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }

        val visibleItem by remember(scheduleListState) {
            derivedStateOf { scheduleListState.firstVisibleItemIndex }
        }

        LaunchedEffect(visibleItem) {
            if (scheduleDays.itemCount > 0 && visibleItem < scheduleDays.itemCount) {
                viewModel.updatePagingDate(scheduleDays.peek(visibleItem)?.day)
            }
        }

        PairsList(
            state = scheduleListState,
            isVertical = isVerticalViewer,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            items(
                count = scheduleDays.itemCount,
                key = scheduleDays.itemKey { it.day }
            ) { index ->
                val day = scheduleDays[index]
                if (day != null) {
                    ScheduleDayCard(
                        scheduleDay = day,
                        pairColors = pairColors,
                        onPairClicked = { pair ->
                            onEditorClicked(scheduleId, pair.id)
                        },
                        onLinkClicked = { url ->
                            BrowserUtils.openLink(context, url)
                        },
                        onLinkCopied = {
                            clipboardManager.setText(AnnotatedString((it)))
                            Toast.makeText(context, R.string.link_copied, Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.fillParentMaxWidth()
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PairsList(
    state: LazyListState,
    isVertical: Boolean,
    modifier: Modifier = Modifier,
    content: LazyListScope.() -> Unit
) {
    if (isVertical) {
        LazyColumn(
            state = state,
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(24.dp),
            content = content
        )
    } else {
        LazyRow(
            state = state,
            flingBehavior = rememberSnapFlingBehavior(lazyListState = state),
            contentPadding = PaddingValues(bottom = 72.dp),
            modifier = modifier
                .verticalScroll(rememberScrollState())
                .animateContentSize(),
            content = content
        )
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
