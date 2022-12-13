package com.vereshchagin.nikolay.stankinschedule.schedule.list.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.vereshchagin.nikolay.stankinschedule.core.ui.ext.Zero
import com.vereshchagin.nikolay.stankinschedule.core.ui.theme.Dimen
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.ScheduleInfo
import com.vereshchagin.nikolay.stankinschedule.schedule.list.ui.components.*
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable


@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun ScheduleScreen(
    onScheduleCreate: () -> Unit,
    onScheduleClicked: (scheduleId: Long) -> Unit,
    viewModel: ScheduleScreenViewModel,
    modifier: Modifier = Modifier,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(
        state = rememberTopAppBarState()
    )

    val editableMode by viewModel.editableMode.collectAsState()

    BackHandler(enabled = editableMode) {
        viewModel.setEditable(false)
    }

    val reorderState = rememberReorderableLazyListState(
        onMove = { from, to ->
            viewModel.schedulesMove(from.index, to.index)
        },
        canDragOver = { true }
    )
    val schedules by viewModel.schedules.collectAsState()
    val favorite by viewModel.favorite.collectAsState()

    var isScheduleRemove by remember { mutableStateOf<ScheduleInfo?>(null) }
    var isSelectedRemove by remember { mutableStateOf<Int?>(null) }

    isScheduleRemove?.let {
        ScheduleRemoveDialog(
            text = stringResource(R.string.schedule_single_remove, it.scheduleName),
            onRemove = {
                viewModel.removeSchedule(it)
                isScheduleRemove = null
            },
            onDismiss = {
                isScheduleRemove = null
            }
        )
    }

    isSelectedRemove?.let {
        ScheduleRemoveDialog(
            text = stringResource(R.string.schedule_selected_remove, it),
            onRemove = {
                viewModel.removeSelectedSchedules()
                isSelectedRemove = null
            },
            onDismiss = {
                isSelectedRemove = null
            }
        )
    }

    Scaffold(
        topBar = {
            if (editableMode) {
                ScheduleActionToolbar(
                    selectedCount = viewModel.selected.count { it.value },
                    onActionClose = {
                        viewModel.setEditable(false)
                    },
                    onRemoveSelected = {
                        isSelectedRemove = it
                    },
                    scrollBehavior = scrollBehavior
                )
            } else {
                ScheduleToolBar(
                    onActionMode = { viewModel.setEditable(true) },
                    scrollBehavior = scrollBehavior
                )
            }
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = !reorderState.listState.isScrollInProgress && !editableMode,
                enter = scaleIn(),
                exit = scaleOut()
            ) {
                FloatingActionButton(
                    onClick = onScheduleCreate
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_add),
                        contentDescription = null
                    )
                }
            }
        },
        contentWindowInsets = WindowInsets.Zero,
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Text(
                text = stringResource(R.string.schedule_my_list),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(
                    horizontal = Dimen.ContentPadding * 2,
                    vertical = Dimen.ContentPadding
                )
            )

            androidx.compose.material.Divider()

            LazyColumn(
                state = reorderState.listState,
                verticalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .reorderable(reorderState)
            ) {
                schedules?.let { data ->
                    // Если нет расписаний
                    if (data.isEmpty()) {
                        item {
                            Text(
                                text = stringResource(R.string.no_schedules),
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillParentMaxWidth()
                                    .padding(Dimen.ContentPadding)
                            )
                        }
                    }

                    // Список с расписаниями
                    items(data, key = { it.id }) { schedule ->
                        if (editableMode) {
                            ReorderableItem(
                                reorderableState = reorderState,
                                key = schedule.id
                            ) {
                                ScheduleActionItem(
                                    schedule = schedule,
                                    isSelected = viewModel.isSelected(schedule.id),
                                    onClicked = {
                                        viewModel.selectSchedule(schedule.id)
                                    },
                                    reorderedState = reorderState,
                                    modifier = Modifier.fillParentMaxWidth()
                                )
                            }
                        } else {
                            ScheduleItem(
                                schedule = schedule,
                                isFavorite = favorite == schedule.id,
                                onClicked = {
                                    onScheduleClicked(schedule.id)
                                },
                                onLongClicked = {
                                    viewModel.setEditable(true)
                                    viewModel.selectSchedule(schedule.id)
                                },
                                onScheduleFavorite = {
                                    viewModel.setFavorite(schedule.id)
                                },
                                onScheduleRemove = {
                                    isScheduleRemove = schedule
                                },
                                modifier = Modifier.fillParentMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}