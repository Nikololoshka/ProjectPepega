package com.vereshchagin.nikolay.stankinschedule.schedule.ui.list.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import com.vereshchagin.nikolay.stankinschedule.schedule.R

@Composable
fun ScheduleCreateDialog(
    state: CreateState,
    onDismiss: () -> Unit,
    onCreate: (scheduleName: String) -> Unit,
    emptyName: String = stringResource(R.string.new_schedule),
) {
    var showExistError by remember { mutableStateOf(false) }
    var currentValue by remember { mutableStateOf(emptyName) }

    LaunchedEffect(state) {
        showExistError = when (state) {
            is CreateState.New -> false
            is CreateState.AlreadyExist -> true
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = stringResource(R.string.create_schedule))
        },
        text = {
            Column {
                OutlinedTextField(
                    value = currentValue,
                    onValueChange = {
                        currentValue = it
                        showExistError = false
                    },
                    isError = currentValue.isEmpty(),
                    singleLine = true,
                )

                if (showExistError) {
                    Text(
                        text = "Schedule with current name already exists!",
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
                        onCreate(currentValue)
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
                    text = stringResource(R.string.schedule_cancel)
                )
            }
        }
    )
}