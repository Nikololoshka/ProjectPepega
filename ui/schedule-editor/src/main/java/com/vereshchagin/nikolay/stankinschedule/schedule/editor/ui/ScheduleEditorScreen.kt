package com.vereshchagin.nikolay.stankinschedule.schedule.editor.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.vereshchagin.nikolay.stankinschedule.core.ui.components.BackButton
import com.vereshchagin.nikolay.stankinschedule.core.ui.theme.Dimen
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.Type
import com.vereshchagin.nikolay.stankinschedule.schedule.core.ui.components.PairFormatter
import com.vereshchagin.nikolay.stankinschedule.schedule.core.ui.R as R_core

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleEditorScreen(
    scheduleId: Long?,
    onBackClicked: () -> Unit,
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
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->

        val disciplines by viewModel.disciplines.collectAsState()
        val pairFormatter = remember { PairFormatter() }

        LazyColumn(
            modifier = Modifier.padding(innerPadding)
        ) {
            items(
                items = disciplines,
                key = { it.discipline }
            ) { disciplineItem ->
                DisciplineItemCard(
                    disciplineItem = disciplineItem,
                    formatter = pairFormatter,
                    modifier = Modifier
                        .fillParentMaxWidth()
                        .padding(Dimen.ContentPadding)
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DisciplineItemCard(
    disciplineItem: ScheduleEditorDiscipline,
    formatter: PairFormatter,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = disciplineItem.discipline,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(Dimen.ContentPadding)
        )

        disciplineItem.forEach { (type, pairs) ->
            Text(
                text = stringResource(
                    id = when (type) {
                        Type.LECTURE -> R_core.string.type_lecture
                        Type.SEMINAR -> R_core.string.type_seminar
                        Type.LABORATORY -> R_core.string.type_laboratory
                    }
                ),
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(horizontal = Dimen.ContentPadding)
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp),
                modifier = Modifier.padding(
                    horizontal = Dimen.ContentPadding,
                    vertical = 4.dp,
                )
            ) {
                pairs.forEach { pair ->
                    SuggestionChip(
                        onClick = { },
                        label = {
                            Text(text = formatter.format(pair.date))
                        },
                        modifier = Modifier.defaultMinSize(minHeight = 38.dp)
                    )
                }
                AssistChip(
                    onClick = { },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_add_more),
                            contentDescription = null
                        )
                    },
                    label = {
                        Text(text = "Add more")
                    },
                    modifier = Modifier.defaultMinSize(minHeight = 38.dp)
                )
            }
        }
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