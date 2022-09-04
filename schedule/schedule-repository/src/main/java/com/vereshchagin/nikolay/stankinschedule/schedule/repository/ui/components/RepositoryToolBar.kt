package com.vereshchagin.nikolay.stankinschedule.schedule.repository.ui.components

import androidx.compose.material.BackdropScaffoldState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.TopAppBar
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.vereshchagin.nikolay.stankinschedule.core.ui.components.BackButton
import com.vereshchagin.nikolay.stankinschedule.schedule.repository.R
import kotlinx.coroutines.launch
import com.vereshchagin.nikolay.stankinschedule.core.R as R_core

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun RepositoryToolBar(
    scaffoldState: BackdropScaffoldState,
    onBackPressed: () -> Unit,
    onRefreshRepository: () -> Unit,
) {
    var showMenu by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    CompositionLocalProvider(
        LocalContentColor provides MaterialTheme.colorScheme.onSecondaryContainer
    ) {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(R.string.repository_title),
                    style = MaterialTheme.typography.titleLarge
                )
            },
            navigationIcon = {
                if (scaffoldState.isRevealed) {
                    IconButton(
                        onClick = { scope.launch { scaffoldState.conceal() } }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_backdrop_close),
                            contentDescription = null
                        )
                    }
                } else {
                    BackButton(
                        onClick = onBackPressed,
                    )
                }
            },
            actions = {
                IconButton(
                    onClick = {
                        if (scaffoldState.isRevealed) {
                            scope.launch { scaffoldState.conceal() }
                        } else {
                            scope.launch { scaffoldState.reveal() }
                        }
                    }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_backdrop_tune),
                        contentDescription = null,
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
                            Text(text = stringResource(R.string.repository_refresh))
                        },
                        onClick = {
                            onRefreshRepository()
                            showMenu = false
                        }
                    )
                }
            },
            backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        )
    }
}