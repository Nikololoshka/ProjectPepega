package com.vereshchagin.nikolay.stankinschedule.schedule.editor.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.vereshchagin.nikolay.stankinschedule.core.ui.components.BackButton
import com.vereshchagin.nikolay.stankinschedule.core.ui.theme.Dimen
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.Type
import com.vereshchagin.nikolay.stankinschedule.schedule.core.ui.PairColors
import com.vereshchagin.nikolay.stankinschedule.schedule.core.ui.components.PairFormatter
import com.vereshchagin.nikolay.stankinschedule.schedule.core.ui.pairTextColor
import com.vereshchagin.nikolay.stankinschedule.schedule.core.ui.toColor
import com.vereshchagin.nikolay.stankinschedule.schedule.core.ui.R as R_core

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ScheduleEditorScreen(
    scheduleId: Long?,
    onBackClicked: () -> Unit,
    onAddPairClicked: (preset: FormPreset?) -> Unit,
    onEditPairClicked: (pairId: Long) -> Unit,
    viewModel: ScheduleEditorViewModel,
    modifier: Modifier = Modifier,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(
        state = rememberTopAppBarState()
    )

    val scheduleName by viewModel.scheduleName.collectAsState()
    LaunchedEffect(scheduleId) {
        viewModel.loadSchedule(scheduleId)
    }

    Scaffold(
        topBar = {
            ScheduleEditorToolBar(
                scheduleName = scheduleName,
                onBackClicked = onBackClicked,
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { onAddPairClicked(null) }) {
                Icon(
                    painter = painterResource(R.drawable.ic_add_more),
                    contentDescription = null
                )
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->

        val disciplines by viewModel.disciplines.collectAsState()
        val scheduleGroupColors by viewModel.scheduleColors.collectAsState()
        val scheduleColors by remember { derivedStateOf { scheduleGroupColors.toColor() } }

        val pairFormatter = remember { PairFormatter() }

        LazyColumn(
            contentPadding = PaddingValues(bottom = 80.dp),
            modifier = Modifier.padding(innerPadding)
        ) {
            disciplines.forEach { (title, items) ->
                stickyHeader(key = title) {
                    Text(
                        text = title,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier
                            .fillParentMaxWidth()
                            .background(MaterialTheme.colorScheme.secondaryContainer)
                            .padding(Dimen.ContentPadding)
                    )
                }

                items(items = items, key = { it.key }) { disciplineItem ->
                    when (disciplineItem) {
                        is ScheduleDiscipline.SchedulePairDiscipline -> {
                            DisciplinePairItem(
                                item = disciplineItem,
                                formatter = pairFormatter,
                                modifier = Modifier
                                    .clickable { onEditPairClicked(disciplineItem.pair.info.id) }
                                    .fillParentMaxWidth()
                                    .padding(
                                        start = Dimen.ContentPadding * 2,
                                        end = Dimen.ContentPadding,
                                        bottom = Dimen.ContentPadding,
                                        top = Dimen.ContentPadding,
                                    )
                            )
                        }

                        is ScheduleDiscipline.ScheduleTypeDiscipline -> {
                            DisciplineTypeItem(
                                item = disciplineItem,
                                colors = scheduleColors,
                                onAddMoreClick = {
                                    onAddPairClicked(
                                        FormPreset(title = title, type = disciplineItem.type)
                                    )
                                },
                                modifier = Modifier
                                    .fillParentMaxWidth()
                                    .padding(horizontal = Dimen.ContentPadding)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DisciplineTypeItem(
    item: ScheduleDiscipline.ScheduleTypeDiscipline,
    colors: PairColors,
    onAddMoreClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when (item.type) {
        Type.LECTURE -> colors.lectureColor
        Type.SEMINAR -> colors.seminarColor
        Type.LABORATORY -> colors.laboratoryColor
    }

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Text(
            text = stringResource(
                id = when (item.type) {
                    Type.LECTURE -> R_core.string.type_lecture
                    Type.SEMINAR -> R_core.string.type_seminar
                    Type.LABORATORY -> R_core.string.type_laboratory
                }
            ),
            color = pairTextColor(backgroundColor),
            style = TextStyle(
                platformStyle = PlatformTextStyle(includeFontPadding = true)
            ),
            modifier = Modifier
                .background(
                    color = backgroundColor,
                    shape = RoundedCornerShape(percent = 50)
                )
                .padding(4.dp)
        )
        IconButton(onClick = onAddMoreClick) {
            Icon(
                painter = painterResource(R.drawable.ic_add_more),
                contentDescription = null,
            )
        }
    }
}

@Composable
private fun DisciplinePairItem(
    item: ScheduleDiscipline.SchedulePairDiscipline,
    formatter: PairFormatter,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = formatter.format(item.pair.date) + ", " + item.pair.time.toString()
        )
        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(stringResource(R.string.editor_lecturer_label))
                }
                append(": ")
                append(item.pair.lecturer.ifEmpty { "---" })
            }
        )
        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(stringResource(R.string.editor_classroom_label))
                }
                append(": ")
                append(item.pair.classroom.ifEmpty { "---" })
            }
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScheduleEditorToolBar(
    scheduleName: String?,
    onBackClicked: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = stringResource(R.string.editor_title),
                    style = MaterialTheme.typography.titleLarge
                )
                scheduleName?.let { currentScheduleName ->
                    Text(
                        text = currentScheduleName,
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            }
        },
        navigationIcon = {
            BackButton(onClick = onBackClicked)
        },
        scrollBehavior = scrollBehavior
    )
}