package com.vereshchagin.nikolay.stankinschedule.schedule.creator.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.vereshchagin.nikolay.stankinschedule.schedule.creator.R
import kotlinx.coroutines.delay
import com.vereshchagin.nikolay.stankinschedule.core.R as R_core

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun ScheduleCreateDialog(
    state: CreateState,
    onDismiss: () -> Unit,
    onCreate: (scheduleName: String) -> Unit,
    onShowKeyBoard: () -> Unit,
) {
    var showExistError by remember { mutableStateOf(false) }
    var showCreateError by remember { mutableStateOf(false) }

    var currentValue by rememberSaveable { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(state) {
        showExistError = state is CreateState.AlreadyExist
        showCreateError = state is CreateState.Error

        if (state is CreateState.New) {
            currentValue = ""

            focusRequester.requestFocus()
            delay(1300)
            onShowKeyBoard()
        }
        if (state is CreateState.Success) {
            onDismiss()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = stringResource(R.string.create_schedule))
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                OutlinedTextField(
                    value = currentValue,
                    onValueChange = {
                        currentValue = it
                        showExistError = false
                        showCreateError = false
                    },
                    singleLine = true,
                    isError = showExistError || showCreateError,
                    label = { Text(text = stringResource(R.string.new_schedule_name)) },
                    modifier = Modifier.focusRequester(focusRequester)
                )

                AnimatedVisibility(visible = showExistError) {
                    Text(
                        text = stringResource(R.string.schedule_exists),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                AnimatedVisibility(visible = showCreateError) {
                    Text(
                        text = stringResource(R.string.schedule_create_error),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (currentValue.isNotEmpty()) {
                        onCreate(currentValue.trim())
                    }
                },
                enabled = currentValue.isNotEmpty() && !showExistError
            ) {
                Text(
                    text = stringResource(R.string.create)
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(
                    text = stringResource(R_core.string.cancel)
                )
            }
        },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    )
}