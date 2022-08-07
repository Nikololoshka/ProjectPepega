package com.vereshchagin.nikolay.stankinschedule.schedule.ui.repository.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import com.vereshchagin.nikolay.stankinschedule.schedule.R

@Composable
fun ChooseNameDialog(
    scheduleName: String,
    onRename: (scheduleName: String) -> Unit,
    onDismiss: () -> Unit,
) {
    var currentValue by remember { mutableStateOf(scheduleName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = stringResource(R.string.repository_choose_name))
        },
        text = {
            OutlinedTextField(
                value = currentValue,
                onValueChange = { currentValue = it },
                isError = currentValue.isEmpty(),
                singleLine = true,
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (currentValue.isNotEmpty()) {
                        onRename(currentValue)
                    }
                },
                enabled = currentValue.isNotEmpty()
            ) {
                Text(text = stringResource(R.string.repository_ok))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(text = stringResource(R.string.repository_cancel))
            }
        }
    )
}