package com.vereshchagin.nikolay.stankinschedule.schedule.table.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.vereshchagin.nikolay.stankinschedule.core.ui.components.RadioGroup
import com.vereshchagin.nikolay.stankinschedule.core.ui.components.RadioItem
import com.vereshchagin.nikolay.stankinschedule.schedule.table.ui.R
import com.vereshchagin.nikolay.stankinschedule.core.ui.R as R_core


class TableFormatDialogState internal constructor(
    internal val onFormatSelected: (format: ExportFormat) -> Unit,
    internal val isShow: MutableState<Boolean>
) {
    fun showDialog() {
        isShow.value = true
    }
}

@Composable
fun rememberFormatDialogState(
    onFormatSelected: (format: ExportFormat) -> Unit,
): TableFormatDialogState {
    val isShow = remember { mutableStateOf(false) }
    return remember(onFormatSelected) {
        TableFormatDialogState(onFormatSelected, isShow)
    }
}

@Composable
fun TableFormatDialog(
    title: String,
    state: TableFormatDialogState,
    modifier: Modifier = Modifier
) {
    if (state.isShow.value) {
        var currentFormat by remember { mutableStateOf(ExportFormat.Image) }

        AlertDialog(
            title = { Text(text = title) },
            text = {
                RadioGroup(
                    title = stringResource(R.string.choose_format)
                ) {
                    RadioItem(
                        title = stringResource(R.string.format_image),
                        selected = currentFormat == ExportFormat.Image,
                        onClick = { currentFormat = ExportFormat.Image },
                    )
                    RadioItem(
                        title = stringResource(R.string.format_pdf),
                        selected = currentFormat == ExportFormat.Pdf,
                        onClick = { currentFormat = ExportFormat.Pdf },
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