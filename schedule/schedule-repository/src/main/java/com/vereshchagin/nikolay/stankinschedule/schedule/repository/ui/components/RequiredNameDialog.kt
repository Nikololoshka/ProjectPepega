package com.vereshchagin.nikolay.stankinschedule.schedule.repository.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.vereshchagin.nikolay.stankinschedule.schedule.repository.R
import com.vereshchagin.nikolay.stankinschedule.core.R as R_core

@Composable
fun RequiredNameDialog(
    scheduleName: String,
    onRename: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = stringResource(R.string.repository_schedule_exist))
        },
        text = {
            Text(text = stringResource(R.string.repository_schedule_exist_text, scheduleName))
        },
        confirmButton = {
            TextButton(
                onClick = onRename
            ) {
                Text(text = stringResource(R.string.repository_rename))
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