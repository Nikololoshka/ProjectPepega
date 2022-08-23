package com.vereshchagin.nikolay.stankinschedule.schedule.editor.ui

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
import com.vereshchagin.nikolay.stankinschedule.core.ui.theme.Dimen
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.Subgroup
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.Time
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.Type
import com.vereshchagin.nikolay.stankinschedule.schedule.editor.R
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun PairEditorScreen(
    pairId: Long?,
    onBackClicked: () -> Unit,
    viewModel: PairEditorViewModel,
    modifier: Modifier = Modifier,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(
        state = rememberTopAppBarScrollState()
    )

    val sheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope()

    var request by remember { mutableStateOf<DateEditRequest?>(null) }

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetBackgroundColor = MaterialTheme.colorScheme.background,
        sheetContent = {
            DateEditorBottomSheet(
                request = null,
                viewModel = viewModel,
            )
        }
    ) {

        Scaffold(
            topBar = {
                EditorToolbar(
                    onApplyClicked = {

                    },
                    onBackClicked = onBackClicked,
                    scrollBehavior = scrollBehavior
                )
            },
            modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
        ) { innerPadding ->

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(Dimen.ContentPadding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {


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
                val endTimesCalc by derivedStateOf {
                    endTimes.slice(startTimes.indexOf(startTime) until endTimes.size)
                }
                var endTime by rememberSaveable { mutableStateOf(endTimes.first()) }

                val pairState by viewModel.pair.collectAsState()

                LaunchedEffect(pairId) {
                    viewModel.loadPair(pairId)
                }

                LaunchedEffect(pairState) {
                    val currentState = pairState
                    if (currentState is com.vereshchagin.nikolay.stankinschedule.core.ui.UIState.Success) {
                        if (currentState.data != null && !viewModel.isInitial) {
                            val initial = currentState.data!!

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
                }


                OutlinedTextField(
                    value = titleField,
                    onValueChange = { titleField = it },
                    label = { Text(text = stringResource(R.string.editor_title_label)) },
                    modifier = Modifier.fillMaxWidth()
                )


                OutlinedTextField(
                    value = lecturerField,
                    onValueChange = { lecturerField = it },
                    label = { Text(text = stringResource(R.string.editor_lecturer_label)) },
                    modifier = Modifier.fillMaxWidth()
                )


                OutlinedTextField(
                    value = classroomField,
                    onValueChange = { classroomField = it },
                    label = { Text(text = stringResource(R.string.editor_classroom_label)) },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedSelectField(
                    value = typeField,
                    onValueChanged = {
                        typeField = it
                    },
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
                    value = subgroupField,
                    onValueChanged = {
                        subgroupField = it
                    },
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
                            startTime = it

                            val s = startTimes.indexOf(it)
                            val e = endTimes.indexOf(endTime)

                            if (s - e > 0 && s in endTimes.indices) {
                                endTime = endTimes[s]
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
                        onValueChanged = {
                            endTime = it
                        },
                        items = endTimesCalc,
                        menuLabel = { it },
                        label = {
                            Text(text = stringResource(R.string.editor_time_end))
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Text(
                    text = "Dates",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp)
                )

                IconButton(
                    onClick = { scope.launch { sheetState.show() } },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_add_date),
                        contentDescription = null
                    )
                }
            }
        }
    }
}