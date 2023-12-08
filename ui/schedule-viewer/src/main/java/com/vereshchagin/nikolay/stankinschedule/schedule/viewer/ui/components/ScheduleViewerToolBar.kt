package com.vereshchagin.nikolay.stankinschedule.schedule.viewer.ui.components

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.vereshchagin.nikolay.stankinschedule.core.ui.components.BackButton
import com.vereshchagin.nikolay.stankinschedule.schedule.viewer.ui.R
import com.vereshchagin.nikolay.stankinschedule.core.ui.R as R_core

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleViewerToolBar(
    scheduleName: String,
    onBackClicked: () -> Unit,
    onEditClicked: () -> Unit,
    onDayChangeClicked: () -> Unit,
    onAddClicked: () -> Unit,
    onRemoveSchedule: () -> Unit,
    onRenameSchedule: () -> Unit,
    onSaveToDevice: () -> Unit,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    var showMenu by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            Text(
                text = scheduleName,
                style = MaterialTheme.typography.titleLarge,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
            )
        },
        navigationIcon = {
            BackButton(onClick = onBackClicked)
        },
        actions = {
            IconButton(
                onClick = onEditClicked
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_schedule_edit),
                    contentDescription = null
                )
            }

            IconButton(
                onClick = onDayChangeClicked
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_date_picker),
                    contentDescription = null
                )
            }

            IconButton(onClick = { showMenu = !showMenu }) {
                Icon(
                    painter = painterResource(R_core.drawable.ic_action_more),
                    contentDescription = null
                )
            }

            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                DropdownMenuItem(
                    text = {
                        Text(text = stringResource(R.string.schedule_add_pair))
                    },
                    onClick = {
                        onAddClicked()
                        showMenu = false
                    }
                )
                DropdownMenuItem(
                    text = {
                        Text(text = stringResource(R.string.save_to_device))
                    },
                    onClick = {
                        onSaveToDevice()
                        showMenu = false
                    }
                )
                DropdownMenuItem(
                    text = {
                        Text(text = stringResource(R.string.rename_schedule))
                    },
                    onClick = {
                        onRenameSchedule()
                        showMenu = false
                    }
                )
                DropdownMenuItem(
                    text = {
                        Text(text = stringResource(R.string.remove_schedule))
                    },
                    onClick = {
                        onRemoveSchedule()
                        showMenu = false
                    }
                )
            }
        },
        scrollBehavior = scrollBehavior,
        modifier = modifier
    )
}