package com.vereshchagin.nikolay.stankinschedule.schedule.table.ui

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.vereshchagin.nikolay.stankinschedule.core.ui.components.FileSaveDialogs
import com.vereshchagin.nikolay.stankinschedule.core.ui.components.rememberFileSaveState
import com.vereshchagin.nikolay.stankinschedule.core.ui.ext.shareDataIntent
import com.vereshchagin.nikolay.stankinschedule.schedule.table.domain.model.TableMode
import com.vereshchagin.nikolay.stankinschedule.schedule.table.ui.components.*
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun ScheduleTableScreen(
    scheduleId: Long,
    viewModel: ScheduleTableViewModel,
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(state = rememberTopAppBarState())

    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )
    val sheetScope = rememberCoroutineScope()
    BackHandler(enabled = sheetState.isVisible) {
        sheetScope.launch { sheetState.hide() }
    }

    val schedule by viewModel.scheduleName.collectAsState()
    LaunchedEffect(scheduleId) {
        viewModel.loadSchedule(scheduleId)
    }

    val image by viewModel.image.collectAsState()

    val color = MaterialTheme.colorScheme.onBackground
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val longScreenSize by remember(configuration, density) {
        derivedStateOf {
            val size = if (configuration.screenWidthDp > configuration.screenHeightDp) {
                configuration.screenWidthDp
            } else {
                configuration.screenHeightDp
            }
            with(density) { size.dp.toPx() }
        }
    }

    var tableMode by rememberSaveable { mutableStateOf(TableMode.Full) }
    var pageNumber by rememberSaveable { mutableStateOf(0) }
    var showUI by rememberSaveable { mutableStateOf(true) }

    LaunchedEffect(color, configuration, tableMode, pageNumber) {
        viewModel.setConfig(
            color = color.toArgb(),
            longScreenSize = longScreenSize,
            mode = tableMode,
            pageNumber = pageNumber
        )
    }

    val sendFormatState = rememberFormatDialogState(
        onFormatSelected = viewModel::sendSchedule
    )

    val saveState = rememberFileSaveState(
        onPickerResult = { uri -> if (uri != null) viewModel.saveSchedule(uri) }
    )
    val saveFormatState = rememberFormatDialogState(
        onFormatSelected = { format ->
            viewModel.setSaveFormat(format)
            saveState.save(
                fileName = schedule.ifEmpty { "null" },
                fileType = format.memeType
            )
        }
    )

    val exportProgress by viewModel.exportProgress.collectAsState()

    val context = LocalContext.current
    LaunchedEffect(exportProgress) {
        val progress = exportProgress

        if (progress is ExportProgress.Finished) {
            if (progress.type == ExportType.Send) {
                val intent = shareDataIntent(progress.path, progress.format.memeType)
                val choose = Intent.createChooser(intent, null)
                context.startActivity(choose)
            }
        }
    }

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetBackgroundColor = MaterialTheme.colorScheme.background,
        sheetContent = {
            SettingsSheet(
                tableMode = tableMode,
                onTableModeChanged = { tableMode = it },
                onSendCopyClicked = {
                    sendFormatState.showDialog()
                    sheetScope.launch { sheetState.hide() }
                },
                onSaveClicked = {
                    saveFormatState.showDialog()
                    sheetScope.launch { sheetState.hide() }
                },
                modifier = Modifier.navigationBarsPadding()
            )
        },
    ) {
        TableFormatDialog(
            title = stringResource(R.string.send_copy),
            state = sendFormatState
        )

        TableFormatDialog(
            title = stringResource(R.string.save_as),
            state = saveFormatState
        )

        FileSaveDialogs(
            state = saveState
        )

        Box(
            modifier = modifier
        ) {
            ZoomableBox(
                minScale = 1f,
                maxScale = 8f,
                onTap = { showUI = !showUI },
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
            ) { scale, offsetX, offsetY ->

                LaunchedEffect(scale) {
                    scrollBehavior.state.contentOffset = if (scale > 1f) -100f else 0f
                }

                with(image) {
                    if (this != null) {
                        Image(
                            bitmap = this,
                            contentDescription = null,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .graphicsLayer(
                                    scaleX = scale,
                                    scaleY = scale,
                                    translationX = offsetX,
                                    translationY = offsetY
                                ),
                        )
                    } else {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            }

            AnimatedVisibility(
                visible = showUI,
                enter = slideInVertically() + fadeIn(),
                exit = slideOutVertically() + fadeOut(),
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopStart)
            ) {
                ScheduleTableAppBar(
                    scheduleName = schedule,
                    onBackClicked = onBackClicked,
                    scrollBehavior = scrollBehavior
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomStart)
            ) {
                AnimatedVisibility(
                    visible = exportProgress !is ExportProgress.Nothing,
                    enter = slideInVertically { it } + fadeIn(),
                    exit = slideOutVertically { it } + fadeOut(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ExportSnackBar(
                        progress = exportProgress,
                        onOpen = { progress ->
                            val intent = Intent().apply {
                                action = Intent.ACTION_VIEW
                                setDataAndType(progress.path, progress.format.memeType)
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            }
                            context.startActivity(Intent.createChooser(intent, null))
                        },
                        onCancelJob = viewModel::cancelExport,
                        onClose = viewModel::exportFinished,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    )
                }

                AnimatedVisibility(
                    visible = showUI,
                    enter = slideInVertically { it } + fadeIn(),
                    exit = slideOutVertically { it } + fadeOut(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ScheduleTableBottomAppBar(
                        tableMode = tableMode,
                        onTableModeChanged = { tableMode = it },
                        page = pageNumber,
                        onBackClicked = { --pageNumber },
                        onNextClicked = { ++pageNumber },
                        onSettingsClicked = { sheetScope.launch { sheetState.show() } }
                    )
                }
            }
        }
    }
}
