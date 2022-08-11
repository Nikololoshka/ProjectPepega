package com.vereshchagin.nikolay.stankinschedule.schedule.ui.list.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.vereshchagin.nikolay.stankinschedule.schedule.R


@Composable
fun ScheduleRemoveDialog(
    text: String,
    onRemove: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = stringResource(R.string.schedule_remove_title))
        },
        text = {
            Text(text = text)
        },
        confirmButton = {
            TextButton(
                onClick = onRemove
            ) {
                Text(text = stringResource(R.string.schedule_remove))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(text = stringResource(R.string.schedule_cancel))
            }
        }
    )
}