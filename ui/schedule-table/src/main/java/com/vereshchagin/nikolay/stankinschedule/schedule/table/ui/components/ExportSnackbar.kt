package com.vereshchagin.nikolay.stankinschedule.schedule.table.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.vereshchagin.nikolay.stankinschedule.schedule.table.ui.R
import kotlinx.coroutines.delay
import com.vereshchagin.nikolay.stankinschedule.core.ui.R as R_core


@Composable
fun ExportSnackBar(
    progress: ExportProgress,
    onOpen: (progress: ExportProgress.Finished) -> Unit,
    onCancelJob: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isRunning by remember(progress) { derivedStateOf { progress is ExportProgress.Running } }

    LaunchedEffect(progress) {
        if (progress is ExportProgress.Error) {
            delay(10000)
            onClose()
        }
        if (progress is ExportProgress.Finished) {
            delay(5000)
            onClose()
        }
    }

    Snackbar(
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = when (progress) {
                        is ExportProgress.Error -> "Error: " + progress.error.toString()
                        is ExportProgress.Finished -> stringResource(R.string.exported)
                        else -> stringResource(R.string.export_schedule)
                    },
                )

                if (isRunning) {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp)
                    )
                }
            }

            if (isRunning) {
                TextButton(onClick = onCancelJob) {
                    Text(
                        text = stringResource(R_core.string.cancel),
                        color = MaterialTheme.colorScheme.primaryContainer
                    )
                }
            }
            if (progress is ExportProgress.Finished && progress.type == ExportType.Save) {
                TextButton(
                    onClick = { onOpen(progress) }
                ) {
                    Text(
                        text = stringResource(R.string.save_open),
                        color = MaterialTheme.colorScheme.primaryContainer
                    )
                }
            }
        }
    }
}