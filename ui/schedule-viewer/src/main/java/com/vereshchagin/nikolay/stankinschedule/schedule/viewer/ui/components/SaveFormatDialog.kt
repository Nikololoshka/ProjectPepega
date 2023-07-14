package com.vereshchagin.nikolay.stankinschedule.schedule.viewer.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.vereshchagin.nikolay.stankinschedule.core.ui.components.RadioGroup
import com.vereshchagin.nikolay.stankinschedule.core.ui.components.RadioItem
import com.vereshchagin.nikolay.stankinschedule.schedule.viewer.ui.R
import com.vereshchagin.nikolay.stankinschedule.core.ui.R as R_core

class SaveFormatDialogState internal constructor(
    internal val onFormatSelected: (format: ExportFormat) -> Unit,
    internal val isShow: MutableState<Boolean>
) {
    fun showDialog() {
        isShow.value = true
    }
}

@Composable
fun rememberSaveFormatDialogState(
    onFormatSelected: (format: ExportFormat) -> Unit,
): SaveFormatDialogState {
    val isShow = remember { mutableStateOf(false) }
    return remember(onFormatSelected) {
        SaveFormatDialogState(onFormatSelected, isShow)
    }
}

@Composable
fun SaveFormatDialog(
    state: SaveFormatDialogState,
    modifier: Modifier = Modifier
) {
    if (state.isShow.value) {
        var currentFormat by remember { mutableStateOf(ExportFormat.Json) }

        AlertDialog(
            title = { Text(text = stringResource(R.string.save_as)) },
            text = {
                RadioGroup(
                    title = stringResource(R.string.choose_format)
                ) {
                    RadioItem(
                        title = stringResource(R.string.format_json),
                        selected = currentFormat == ExportFormat.Json,
                        onClick = { currentFormat = ExportFormat.Json },
                    )
                    RadioItem(
                        title = stringResource(R.string.format_ical),
                        selected = currentFormat == ExportFormat.ICal,
                        onClick = { currentFormat = ExportFormat.ICal },
                    )
                }
            },
            onDismissRequest = { state.isShow.value = false },
            dismissButton = {
                TextButton(
                    onClick = { state.isShow.value = false }
                ) {
                    Text(text = stringResource(R_core.string.cancel))
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { state.onFormatSelected(currentFormat); state.isShow.value = false }
                ) {
                    Text(text = stringResource(R_core.string.ok))
                }
            },
            modifier = modifier
        )
    }
}