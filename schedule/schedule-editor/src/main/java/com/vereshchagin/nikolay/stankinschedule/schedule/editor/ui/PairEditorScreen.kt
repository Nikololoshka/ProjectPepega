package com.vereshchagin.nikolay.stankinschedule.schedule.editor.ui

import androidx.activity.compose.BackHandler
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.flowlayout.FlowRow
import com.vereshchagin.nikolay.stankinschedule.core.ui.theme.Dimen
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.*
import com.vereshchagin.nikolay.stankinschedule.schedule.editor.R
import kotlinx.coroutines.launch
import com.vereshchagin.nikolay.stankinschedule.core.R as R_core


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun PairEditorScreen(
    scheduleId: Long,
    pairId: Long?,
    onBackClicked: () -> Unit,
    viewModel: PairEditorViewModel,
    modifier: Modifier = Modifier,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(
        state = rememberTopAppBarState()
    )

    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden, skipHalfExpanded = true
    )
    val sheetScope = rememberCoroutineScope()

    BackHandler(enabled = sheetState.isVisible) {
        sheetScope.launch { sheetState.hide() }
    }

    var request by remember { mutableStateOf<DateEditorRequest>(DateEditorRequest.New) }
    var isDeletePair by remember { mutableStateOf(false) }

    var titleField by rememberSaveable { mutableStateOf("") }
    var lecturerField by rememberSaveable { mutableStateOf("") }
    var classroomField by rememberSaveable { mutableStateOf("") }

    var typeField: Type by rememberSaveable(
        stateSaver = Saver(save = { it.tag }, restore = { Type.of(it) })
    ) { mutableStateOf(Type.LECTURE) }

    var subgroupField: Subgroup by rememberSaveable(
        stateSaver = Saver(save = { it.tag }, restore = { Subgroup.of(it) })
    ) { mutableStateOf(Subgroup.COMMON) }

    val startTimes = Time.STARTS
    var startTime by rememberSaveable { mutableStateOf(startTimes.first()) }

    val endTimes = Time.ENDS
    var endTime by rememberSaveable { mutableStateOf(endTimes.first()) }

    val date by viewModel.date.collectAsState()

    var scheduleError by remember { mutableStateOf<Exception?>(null) }
    val errorScope = rememberCoroutineScope()
    errorScope.launch {
        viewModel.scheduleErrors.collect { scheduleError = it }
    }

    val pairState by viewModel.pair.collectAsState()

    LaunchedEffect(scheduleId, pairId) {
        viewModel.loadPair(scheduleId, pairId)
    }

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetBackgroundColor = MaterialTheme.colorScheme.background,
        sheetContent = {
            DateEditorBottomSheet(
                request = request,
                viewModel = viewModel,
                onDismissClicked = { sheetScope.launch { sheetState.hide() } }
            )
        }
    ) {
        Scaffold(
            topBar = {
                EditorToolbar(
                    onApplyClicked = {
                        viewModel.applyPair(
                            title = titleField,
                            lecturer = lecturerField,
                            classroom = classroomField,
                            type = typeField,
                            subgroup = subgroupField,
                            startTime = startTime,
                            endTime = endTime
                        )
                    },
                    onDeleteClicked = { isDeletePair = true },
                    onBackClicked = onBackClicked,
                    scrollBehavior = scrollBehavior
                )
            },
            modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
        ) { innerPadding ->

            if (isDeletePair) {
                AlertDialog(
                    onDismissRequest = { isDeletePair = false },
                    title = {
                        // TODO("Hardcode text)
                        Text(text = "Удаление пары")
                    },
                    text = {
                        Text(text = "Вы уверены, что хотите удалить пару из расписания?")
                    },
                    confirmButton = {
                        TextButton(
                            onClick = { viewModel.deletePair() }
                        ) {
                            Text(text = stringResource(R.string.editor_delete_pair))
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { isDeletePair = false }
                        ) {
                            Text(text = stringResource(R_core.string.cancel))
                        }
                    }
                )
            }

            scheduleError?.let {
                AlertDialog(
                    onDismissRequest = { scheduleError = null },
                    title = { Text(text = stringResource(R_core.string.error)) },
                    text = {
                        Text(
                            text = when (it) {
                                is PairIntersectException -> {
                                    stringResource(R.string.editor_conflict_pair, it.second)
                                }
                                else -> it.toString()
                            }
                        )
                    },
                    confirmButton = {
                        TextButton(
                            onClick = { scheduleError = null }
                        ) {
                            Text(text = stringResource(R_core.string.ok))
                        }
                    }
                )
            }

            LaunchedEffect(pairState) {
                when (val state = pairState) {
                    is PairEditorState.Content -> {
                        if (state.pair != null && !viewModel.isInitial) {
                            val initial = state.pair

                            titleField = initial.title
                            lecturerField = initial.lecturer
                            classroomField = initial.classroom
                            typeField = initial.type
                            subgroupField = initial.subgroup
                            startTime = initial.time.startString()
                            endTime = initial.time.endString()

                            viewModel.setPairInitial()
                        }
                    }
                    is PairEditorState.Complete -> {
                        onBackClicked()
                    }
                    is PairEditorState.Error -> {
                        onBackClicked()
                    }
                    else -> {}
                }
            }

            when (pairState) {
                is PairEditorState.Content -> {
                    EditorContent(
                        title = titleField,
                        onTitleChanged = { titleField = it },
                        lecturer = lecturerField,
                        onLecturerChanged = { lecturerField = it },
                        classroom = classroomField,
                        onClassroomChanged = { classroomField = it },
                        type = typeField,
                        onTypeChanged = { typeField = it },
                        subgroup = subgroupField,
                        onSubgroupChanged = { subgroupField = it },
                        startTime = startTime,
                        onStartTimeChanged = { startTime = it },
                        startTimes = startTimes,
                        endTime = endTime,
                        onEndTimeChanged = { endTime = it },
                        endTimes = endTimes,
                        date = date,
                        onDateEdit = { item ->
                            request = DateEditorRequest.Edit(item)
                            sheetScope.launch { sheetState.show() }
                        },
                        onDateNew = {
                            request = DateEditorRequest.New
                            sheetScope.launch { sheetState.show() }
                        },
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(Dimen.ContentPadding)
                    )
                }
                else -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(innerPadding)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditorContent(
    title: String,
    onTitleChanged: (title: String) -> Unit,
    lecturer: String,
    onLecturerChanged: (lecturer: String) -> Unit,
    classroom: String,
    onClassroomChanged: (classroom: String) -> Unit,
    type: Type,
    onTypeChanged: (type: Type) -> Unit,
    subgroup: Subgroup,
    onSubgroupChanged: (subgroup: Subgroup) -> Unit,
    startTime: String,
    onStartTimeChanged: (time: String) -> Unit,
    startTimes: List<String>,
    endTime: String,
    onEndTimeChanged: (time: String) -> Unit,
    endTimes: List<String>,
    date: DateModel,
    onDateEdit: (item: DateItem) -> Unit,
    onDateNew: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val endTimesCalc by derivedStateOf {
        endTimes.slice(startTimes.indexOf(startTime) until endTimes.size)
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(Dimen.ContentPadding),
        modifier = modifier
    ) {

        OutlinedTextField(
            value = title,
            onValueChange = onTitleChanged,
            label = { Text(text = stringResource(R.string.editor_title_label)) },
            modifier = Modifier.fillMaxWidth()
        )


        OutlinedTextField(
            value = lecturer,
            onValueChange = onLecturerChanged,
            label = { Text(text = stringResource(R.string.editor_lecturer_label)) },
            modifier = Modifier.fillMaxWidth()
        )


        OutlinedTextField(
            value = classroom,
            onValueChange = onClassroomChanged,
            label = { Text(text = stringResource(R.string.editor_classroom_label)) },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedSelectField(
            value = type,
            onValueChanged = onTypeChanged,
            items = listOf(
                Type.LECTURE,
                Type.SEMINAR,
                Type.LABORATORY
            ),
            menuLabel = {
                @StringRes val id = when (it) {
                    Type.LECTURE -> R.string.editor_type_lecture
                    Type.SEMINAR -> R.string.editor_type_seminar
                    Type.LABORATORY -> R.string.editor_type_laboratory
                }
                stringResource(id)
            },
            label = { Text(text = stringResource(R.string.editor_type_label)) },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedSelectField(
            value = subgroup,
            onValueChanged = onSubgroupChanged,
            items = listOf(
                Subgroup.COMMON,
                Subgroup.A,
                Subgroup.B
            ),
            menuLabel = {
                @StringRes val id = when (it) {
                    Subgroup.COMMON -> R.string.editor_subgroup_common
                    Subgroup.A -> R.string.editor_subgroup_a
                    Subgroup.B -> R.string.editor_subgroup_b
                }
                stringResource(id)
            },
            label = { Text(text = stringResource(R.string.editor_subgroup_label)) },
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedSelectField(
                value = startTime,
                onValueChanged = {
                    onStartTimeChanged(it)

                    val s = startTimes.indexOf(it)
                    val e = endTimes.indexOf(endTime)

                    if (s - e > 0 && s in endTimes.indices) {
                        onEndTimeChanged(endTimes[s])
                    }
                },
                items = startTimes,
                menuLabel = { it },
                label = {
                    Text(text = stringResource(R.string.editor_time_start))
                },
                modifier = Modifier.fillMaxWidth(0.5f)
            )
            OutlinedSelectField(
                value = endTime,
                onValueChanged = onEndTimeChanged,
                items = endTimesCalc,
                menuLabel = { it },
                label = {
                    Text(text = stringResource(R.string.editor_time_end))
                },
                modifier = Modifier.fillMaxWidth()
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.editor_dates),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp)
            )

            FlowRow(
                mainAxisSpacing = 8.dp,
                crossAxisSpacing = 0.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                date.forEach { item ->
                    DateChip(
                        item = item,
                        onClicked = { onDateEdit(item) },
                        modifier = Modifier.defaultMinSize(minHeight = 38.dp)
                    )
                }

                AssistChip(
                    onClick = onDateNew,
                    label = {
                        Text(text = stringResource(R.string.editor_new_date))
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_add_date),
                            contentDescription = null,
                        )
                    },
                    modifier = Modifier.defaultMinSize(minHeight = 38.dp)
                )
            }
        }
    }

}