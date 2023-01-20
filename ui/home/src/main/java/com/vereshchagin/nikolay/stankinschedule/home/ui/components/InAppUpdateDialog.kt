package com.vereshchagin.nikolay.stankinschedule.home.ui.components

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.vereshchagin.nikolay.stankinschedule.core.domain.ext.subHours
import com.vereshchagin.nikolay.stankinschedule.core.ui.theme.Dimen
import com.vereshchagin.nikolay.stankinschedule.home.ui.R
import com.vereshchagin.nikolay.stankinschedule.home.ui.data.GooglePlayInAppUpdater
import com.vereshchagin.nikolay.stankinschedule.home.ui.data.InAppUpdater
import com.vereshchagin.nikolay.stankinschedule.home.ui.data.UpdateState
import org.joda.time.DateTime


class InAppUpdateState internal constructor(
    private val updateManager: InAppUpdater,
    private val updateLauncher: (info: AppUpdateInfo) -> Unit,
    internal val progress: State<UpdateState>
) {
    internal fun later() {
        updateManager.later()
    }

    internal fun startUpdate(info: AppUpdateInfo) {
        updateLauncher(info)
    }

    internal fun restart() {
        updateManager.completeUpdate()
    }
}

@Composable
fun rememberInAppUpdater(
    saveLastUpdate: (last: DateTime) -> Unit,
    currentLastUpdate: () -> DateTime?
): InAppUpdateState {

    val context = LocalContext.current
    val updateManager: InAppUpdater = remember { GooglePlayInAppUpdater(context) }
    val progress = updateManager.updateState.collectAsState()

    val updateLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = {
            if (it.resultCode == Activity.RESULT_CANCELED) {
                saveLastUpdate(DateTime.now())
            }
        }
    )

    LaunchedEffect(progress) {
        if (progress.value is UpdateState.UpToDate) {
            saveLastUpdate(DateTime.now().minusDays(8))
        }
    }

    LaunchedEffect(currentLastUpdate) {
        val lastUpdate = currentLastUpdate()
        if (lastUpdate == null || lastUpdate subHours DateTime.now() < 24 * 7) {
            updateManager.checkUpdate()
        }
    }

    DisposableEffect(Unit) {
        onDispose { updateManager.onDestroy() }
    }

    return remember(
        saveLastUpdate,
        currentLastUpdate
    ) {
        InAppUpdateState(
            updateManager = updateManager,
            updateLauncher = { info ->
                updateManager.startUpdate(info) { intent, _, fillInIntent, flagsMask, flagsValues, _, _ ->
                    updateLauncher.launch(
                        IntentSenderRequest.Builder(intent)
                            .setFillInIntent(fillInIntent)
                            .setFlags(flagsValues, flagsMask)
                            .build()
                    )
                }
            },
            progress = progress
        )
    }
}

@Composable
fun InAppUpdateDialog(
    state: InAppUpdateState,
    modifier: Modifier = Modifier
) {
    if (state.progress.value !is UpdateState.UpToDate) {
        ElevatedCard(
            modifier = modifier
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimen.ContentPadding)
            ) {
                UpdateContent(
                    progress = state.progress.value,
                    onLater = state::later,
                    onUpdate = state::startUpdate,
                    onRestart = state::restart
                )
            }
        }
    }
}

@Composable
private fun ColumnScope.UpdateContent(
    progress: UpdateState,
    onLater: () -> Unit,
    onUpdate: (info: AppUpdateInfo) -> Unit,
    onRestart: () -> Unit,
) {
    when (progress) {
        is UpdateState.UpdateRequired -> {
            UpdateRequiredContent(
                onLater = onLater,
                onUpdate = { onUpdate(progress.info) }
            )
        }
        is UpdateState.UpdateProgress -> {
            UpdateProgressContent(
                progress = progress
            )
        }
        is UpdateState.UpdateRestart -> {
            UpdateRestartContent(
                onRestart = onRestart
            )
        }
    }
}

@Composable
private fun ColumnScope.UpdateRequiredContent(
    onLater: () -> Unit,
    onUpdate: () -> Unit
) {
    Text(text = stringResource(R.string.update_available))

    Row(
        horizontalArrangement = Arrangement.End,
        modifier = Modifier.fillMaxWidth()
    ) {
        TextButton(onClick = onLater) {
            Text(text = stringResource(R.string.later))
        }
        TextButton(onClick = onUpdate) {
            Text(text = stringResource(R.string.update))
        }
    }
}

@Composable
private fun ColumnScope.UpdateProgressContent(
    progress: UpdateState.UpdateProgress
) {
    Text(
        text = stringResource(R.string.updating),
        modifier = Modifier.padding(bottom = Dimen.ContentPadding)
    )

    if (progress.progress == 0f) {
        LinearProgressIndicator(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Dimen.ContentPadding)
        )
    } else {
        LinearProgressIndicator(
            progress = progress.progress,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Dimen.ContentPadding)
        )
    }
}

@Composable
private fun ColumnScope.UpdateRestartContent(
    onRestart: () -> Unit,
) {
    Text(text = stringResource(R.string.update_restart))

    Row(
        horizontalArrangement = Arrangement.End,
        modifier = Modifier.fillMaxWidth()
    ) {
        TextButton(onClick = onRestart) {
            Text(text = stringResource(R.string.restart))
        }
    }
}