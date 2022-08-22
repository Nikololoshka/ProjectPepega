package com.vereshchagin.nikolay.stankinschedule.schedule.list.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.vereshchagin.nikolay.stankinschedule.schedule.list.R

@Composable
fun ScheduleActionToolbar(
    selectedCount: Int,
    onActionClose: () -> Unit,
    onRemoveSelected: (selected: Int) -> Unit,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    SmallTopAppBar(
        title = {
            Text(
                text = stringResource(R.string.schedule_count_selected, selectedCount),
                style = MaterialTheme.typography.titleLarge
            )
        },
        navigationIcon = {
            IconButton(
                onClick = onActionClose
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_action_close),
                    contentDescription = null
                )
            }
        },
        actions = {
            IconButton(
                onClick = { onRemoveSelected(selectedCount) },
                enabled = selectedCount > 0
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_clear_all),
                    contentDescription = null
                )
            }
        },
        scrollBehavior = scrollBehavior,
        modifier = modifier
    )
}