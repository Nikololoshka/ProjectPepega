package com.vereshchagin.nikolay.stankinschedule.schedule.creator.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.vereshchagin.nikolay.stankinschedule.schedule.creator.ui.R
import com.vereshchagin.nikolay.stankinschedule.core.ui.R as R_core

@Composable
fun ReadPermissionDeniedDialog(
    onDismiss: () -> Unit,
) {
    AlertDialog(
        title = {
            Text(text = stringResource(R.string.read_permission_denied))
        },
        text = {
            Text(text = stringResource(R.string.read_permissions_details))
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R_core.string.ok))
            }
        },
        onDismissRequest = onDismiss,
    )
}