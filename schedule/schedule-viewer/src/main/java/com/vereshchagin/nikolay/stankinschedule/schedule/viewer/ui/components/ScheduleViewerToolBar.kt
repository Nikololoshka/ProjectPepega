package com.vereshchagin.nikolay.stankinschedule.schedule.viewer.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.vereshchagin.nikolay.stankinschedule.core.ui.components.BackButton
import com.vereshchagin.nikolay.stankinschedule.schedule.viewer.R
import com.vereshchagin.nikolay.stankinschedule.core.R as R_core

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleViewerToolBar(
    scheduleName: String,
    onBackClicked: () -> Unit,
    onDayChangeClicked: () -> Unit,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    var showMenu by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            Text(
                text = scheduleName,
                style = MaterialTheme.typography.titleLarge
            )
        },
        navigationIcon = {
            BackButton(onClick = onBackClicked)
        },
        actions = {
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
                        Text(text = "Add pair")
                    },
                    onClick = {
                        showMenu = false
                    }
                )
                DropdownMenuItem(
                    text = {
                        Text(text = "Save to device")
                    },
                    onClick = {
                        showMenu = false
                    }
                )
                DropdownMenuItem(
                    text = {
                        Text(text = "Rename schedule")
                    },
                    onClick = {
                        showMenu = false
                    }
                )
                DropdownMenuItem(
                    text = {
                        Text(text = "Remove schedule")
                    },
                    onClick = {
                        showMenu = false
                    }
                )
            }
        },
        scrollBehavior = scrollBehavior,
        modifier = modifier
    )
}