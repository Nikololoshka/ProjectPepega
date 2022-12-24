package com.vereshchagin.nikolay.stankinschedule.core.ui.components

import android.Manifest
import android.os.Build
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.vereshchagin.nikolay.stankinschedule.core.ui.R
import com.vereshchagin.nikolay.stankinschedule.core.ui.notification.NotificationUtils
import com.vereshchagin.nikolay.stankinschedule.core.ui.theme.AppTheme

class DarkProvider : PreviewParameterProvider<Boolean> {
    override val values: Sequence<Boolean> = sequenceOf(true, false)
}

@Preview(
    showSystemUi = true,
    showBackground = true
)
@Composable
private fun NotificationUpdateDialogPreview(
    @PreviewParameter(DarkProvider::class) dark: Boolean
) {
    val state = rememberNotificationUpdateState(
        isEnabled = true,
        onChanged = {}
    )

    // Debug show
    state._isShow = true

    AppTheme(dark) {
        Box(modifier = Modifier.fillMaxSize()) {
            NotificationUpdateDialog(
                title = "Journal notification",
                content = "Get notification about marks",
                state = state,
            )
        }
    }
}


class NotificationUpdateState internal constructor(
    internal val isEnabled: Boolean,
    private val onChanged: (enable: Boolean) -> Unit,
) {
    internal var _isShow by mutableStateOf(false)

    fun setEnabled(enable: Boolean) {
        if (isEnabled != enable) {
            onChanged(enable)
        }
    }

    fun showDialog() {
        _isShow = true
    }
}

@Composable
fun rememberNotificationUpdateState(
    isEnabled: Boolean,
    onChanged: (enable: Boolean) -> Unit,
): NotificationUpdateState {
    return remember(isEnabled, onChanged) { NotificationUpdateState(isEnabled, onChanged) }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun NotificationUpdateDialog(
    title: String,
    content: String,
    state: NotificationUpdateState,
    modifier: Modifier = Modifier
) {
    if (state._isShow) {
        val context = LocalContext.current
        val isNotificationEnabled by remember(state._isShow, state.isEnabled) {
            derivedStateOf { NotificationUtils.isNotificationAllow(context) }
        }

        val notificationPermission = rememberPermissionState(
            permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Manifest.permission.POST_NOTIFICATIONS
            } else {
                "" // nothing
            },
            onPermissionResult = { isGranted ->
                if (isGranted) {
                    state.setEnabled(true)
                } else {
                    state._isShow = false
                }
            }
        )

        AlertDialog(
            title = { Text(text = title) },
            text = {
                Column {
                    Text(
                        text = content,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    ChooseItem(
                        title = stringResource(R.string.notification_on),
                        selected = state.isEnabled,
                        onClick = {
                            if (isNotificationEnabled) {
                                state.setEnabled(true)
                            } else {
                                notificationPermission.launchPermissionRequest()
                            }
                        },
                        icon = {
                            Icon(
                                painter = painterResource(R.drawable.ic_notifications_on),
                                contentDescription = null
                            )
                        }
                    )

                    ChooseItem(
                        title = stringResource(R.string.notification_off),
                        selected = !state.isEnabled,
                        onClick = { state.setEnabled(false) },
                        icon = {
                            Icon(
                                painter = painterResource(R.drawable.ic_notifications_off),
                                contentDescription = null
                            )
                        }
                    )
                }
            },
            onDismissRequest = { state._isShow = false },
            confirmButton = {
                TextButton(onClick = { state._isShow = false }) {
                    Text(text = stringResource(R.string.ok))
                }
            },
            modifier = modifier,
        )
    }
}

@Composable
private fun ChooseItem(
    title: String,
    selected: Boolean,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                onClick = onClick,
                interactionSource = interactionSource,
                indication = LocalIndication.current,
            )
            .padding(start = 8.dp)
    ) {

        icon()

        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .weight(weight = 1f),
        )

        RadioButton(
            selected = selected,
            onClick = onClick,
            interactionSource = interactionSource,
        )
    }
}