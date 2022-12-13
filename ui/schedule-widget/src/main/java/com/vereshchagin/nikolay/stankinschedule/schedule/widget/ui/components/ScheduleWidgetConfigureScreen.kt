package com.vereshchagin.nikolay.stankinschedule.schedule.widget.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.vereshchagin.nikolay.stankinschedule.core.ui.components.AppScaffold
import com.vereshchagin.nikolay.stankinschedule.core.ui.theme.Dimen
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.Subgroup
import com.vereshchagin.nikolay.stankinschedule.schedule.widget.domain.model.ScheduleItem
import com.vereshchagin.nikolay.stankinschedule.schedule.widget.domain.model.ScheduleWidgetData
import com.vereshchagin.nikolay.stankinschedule.schedule.widget.ui.R
import com.vereshchagin.nikolay.stankinschedule.schedule.widget.ui.configure.ScheduleWidgetConfigureViewModel
import com.vereshchagin.nikolay.stankinschedule.schedule.viewer.ui.R as R_core

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleWidgetConfigureScreen(
    appWidgetId: Int,
    viewModel: ScheduleWidgetConfigureViewModel,
    onBackPressed: () -> Unit,
    onScheduleWidgetChanged: (appWidgetId: Int, data: ScheduleWidgetData) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    AppScaffold(
        topBar = {
            ScheduleWidgetConfigureAppBar(
                onBackPressed = onBackPressed,
                scrollBehavior = scrollBehavior
            )
        },
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        Column(
            verticalArrangement = Arrangement.spacedBy(Dimen.ContentPadding),
            modifier = Modifier
                .padding(innerPadding)
                .padding(Dimen.ContentPadding)
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
        ) {
            val currentWidgetData by viewModel.currentData.collectAsState()

            val schedules by viewModel.schedules.collectAsState()
            var currentSchedule by remember { mutableStateOf(ScheduleItem.NO_ITEM) }
            var currentSubgroup by remember(currentWidgetData) { mutableStateOf(Subgroup.COMMON) }
            var isShowSubgroup by remember(currentWidgetData) { mutableStateOf(true) }

            LaunchedEffect(currentWidgetData) {
                currentWidgetData?.let { data ->
                    currentSchedule = ScheduleItem(
                        data.scheduleName,
                        data.scheduleId
                    )
                    currentSubgroup = data.subgroup
                    isShowSubgroup = data.display
                }
            }

            LaunchedEffect(appWidgetId) {
                viewModel.loadConfigure(appWidgetId)
            }

            OutlinedSelectField(
                value = currentSchedule,
                onValueChanged = { currentSchedule = it },
                items = schedules,
                menuLabel = { it.scheduleName },
                label = { Text(text = stringResource(R.string.widget_schedule)) },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedSelectField(
                value = currentSubgroup,
                onValueChanged = { currentSubgroup = it },
                items = Subgroup.values().toList(),
                menuLabel = {
                    val id = when (it) {
                        Subgroup.COMMON -> R_core.string.subgroup_common
                        Subgroup.A -> R_core.string.subgroup_a
                        Subgroup.B -> R_core.string.subgroup_b
                    }
                    stringResource(id)
                },
                label = { Text(text = stringResource(R.string.widget_subgroup)) },
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.widget_show_subgroup),
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .weight(1f)
                )

                Switch(
                    checked = isShowSubgroup,
                    onCheckedChange = { isShowSubgroup = it }
                )
            }

            Button(
                onClick = {
                    if (currentSchedule != ScheduleItem.NO_ITEM) {
                        val data = ScheduleWidgetData(
                            scheduleName = currentSchedule.scheduleName,
                            scheduleId = currentSchedule.scheduleId,
                            subgroup = currentSubgroup,
                            display = isShowSubgroup
                        )
                        viewModel.saveConfigure(appWidgetId, data)
                        onScheduleWidgetChanged(appWidgetId, data)
                    }
                },
                enabled = currentSchedule != ScheduleItem.NO_ITEM,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(
                        if (currentWidgetData == null) {
                            R.string.widget_add
                        } else {
                            R.string.widget_change
                        }
                    )
                )
            }
        }
    }
}