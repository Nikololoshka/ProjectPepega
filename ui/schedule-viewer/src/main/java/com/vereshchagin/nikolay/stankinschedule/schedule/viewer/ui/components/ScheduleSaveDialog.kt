package com.vereshchagin.nikolay.stankinschedule.schedule.viewer.ui.components

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.google.accompanist.permissions.*
import com.vereshchagin.nikolay.stankinschedule.schedule.viewer.ui.R
import com.vereshchagin.nikolay.stankinschedule.core.ui.R as R_core


@OptIn(ExperimentalPermissionsApi::class)
class ScheduleSaveState internal constructor(
    private val launchPicker: () -> Unit,
    internal val permission: PermissionState,
    internal val showDeniedDialog: MutableState<Boolean>,
) {
    internal var showRationaleDialog = mutableStateOf(false)

    fun save() {
        when {
            isGranted() -> launchPicker()
            permission.status.shouldShowRationale -> showRationaleDialog.value = true
            else -> permission.launchPermissionRequest()
        }
    }

    private fun isGranted(): Boolean {
        return permission.status.isGranted || Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun rememberScheduleSaveController(
    fileName: String,
    onPickerResult: (uri: Uri?) -> Unit
): ScheduleSaveState {

    val writeScheduleLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json"),
        onResult = onPickerResult
    )

    val launchPicker = { writeScheduleLauncher.launch(fileName) }
    val showDeniedDialog = remember { mutableStateOf(false) }

    val writePermission = rememberPermissionState(
        permission = Manifest.permission.WRITE_EXTERNAL_STORAGE,
        onPermissionResult = { isGranted ->
            if (isGranted) launchPicker() else showDeniedDialog.value = true
        }
    )

    return remember(launchPicker) {
        ScheduleSaveState(
            launchPicker = launchPicker,
            permission = writePermission,
            showDeniedDialog = showDeniedDialog
        )
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ScheduleSaveDialogs(
    state: ScheduleSaveState
) {
    if (state.showRationaleDialog.value) {
        AlertDialog(
            title = { Text(text = stringResource(R.string.permission_write_title)) },
            text = { Text(text = stringResource(R.string.permission_write_text)) },
            onDismissRequest = { state.showRationaleDialog.value = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        state.showRationaleDialog.value = false
                        state.permission.launchPermissionRequest()
                    }
                ) {
                    Text(text = stringResource(R_core.string.ok))
                }
            }
        )
    }

    if (state.showDeniedDialog.value) {
        val context = LocalContext.current
        AlertDialog(
            title = { Text(text = stringResource(R.string.permission_write_denied_title)) },
            text = { Text(text = stringResource(R.string.permission_write_denied_text)) },
            onDismissRequest = { state.showDeniedDialog.value = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.parse("package:" + context.packageName)
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            addCategory(Intent.CATEGORY_DEFAULT)
                        }
                        context.startActivity(intent)
                    }
                ) {
                    Text(text = stringResource(R.string.permisson_write_to_settings))
                }
            }
        )
    }
}