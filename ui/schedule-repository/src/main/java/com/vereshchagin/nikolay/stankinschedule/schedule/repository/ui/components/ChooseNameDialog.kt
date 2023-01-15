package com.vereshchagin.nikolay.stankinschedule.schedule.repository.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import com.vereshchagin.nikolay.stankinschedule.schedule.repository.ui.R
import com.vereshchagin.nikolay.stankinschedule.core.ui.R as R_core

@OptIn(ExperimentalMaterial3Api::class)
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
                Text(text = stringResource(R_core.string.ok))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(text = stringResource(R_core.string.cancel))
            }
        }
    )
}