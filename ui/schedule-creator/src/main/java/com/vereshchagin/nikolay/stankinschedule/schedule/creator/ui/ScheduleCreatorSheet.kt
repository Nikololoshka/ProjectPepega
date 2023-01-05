package com.vereshchagin.nikolay.stankinschedule.schedule.creator.ui

import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.vereshchagin.nikolay.stankinschedule.core.ui.theme.Dimen
import com.vereshchagin.nikolay.stankinschedule.schedule.creator.ui.components.*


@OptIn(ExperimentalPermissionsApi::class, ExperimentalComposeUiApi::class)
@Composable
fun ScheduleCreatorSheet(
    onNavigateBack: () -> Unit,
    onRepositoryClicked: () -> Unit,
    onShowSnackBar: (message: String) -> Unit,
    viewModel: ScheduleCreatorViewModel,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    val createState by viewModel.createState.collectAsState()
    LaunchedEffect(createState) {
        if (createState is CreateState.Success) {
            onNavigateBack()
        }
    }

    createState?.let {
        ScheduleCreateDialog(
            state = it,
            onDismiss = {
                viewModel.onCreateSchedule(CreateEvent.Cancel)
            },
            onCreate = { scheduleName ->
                viewModel.createSchedule(scheduleName)
            }
        )
    }

    val importState by viewModel.importState.collectAsState()
    LaunchedEffect(importState) {
        val state = importState
        if (state is ImportState.Success) {
            onShowSnackBar(context.getString(R.string.schedule_added, state.scheduleName))
            onNavigateBack()
        }
        if (state is ImportState.Failed) {
            onShowSnackBar(context.getString(R.string.import_error))
            onNavigateBack()
        }
    }

    var readDeniedDialog by remember { mutableStateOf(false) }
    if (readDeniedDialog) {
        ReadPermissionDeniedDialog(
            onDismiss = { readDeniedDialog = false }
        )
    }

    val openScheduleLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = {
            if (it != null) {
                viewModel.importSchedule(it)
            }
        }
    )

    val readStoragePermission = rememberPermissionState(
        permission = android.Manifest.permission.READ_EXTERNAL_STORAGE,
        onPermissionResult = { isGranted ->
            if (isGranted) {
                openScheduleLauncher.launch(arrayOf("application/json"))
            } else {
                readDeniedDialog = true
            }
        }
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(Dimen.ContentPadding),
        modifier = modifier.padding(top = Dimen.ContentPadding * 2)
    ) {
        Text(
            text = stringResource(R.string.schedule_creator),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.fillMaxWidth()
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            val items = listOf(
                ScheduleCreatorItem(
                    title = R.string.schedule_create_new,
                    icon = R.drawable.ic_schedule_new,
                    onItemClicked = { viewModel.onCreateSchedule(CreateEvent.New) }
                ),
                ScheduleCreatorItem(
                    title = R.string.schedule_from_device,
                    icon = R.drawable.ic_schedule_from_device,
                    onItemClicked = {
                        if (readStoragePermission.status.isGranted || Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            openScheduleLauncher.launch(arrayOf("application/json"))
                        } else {
                            readStoragePermission.launchPermissionRequest()
                        }
                    }
                ),
                ScheduleCreatorItem(
                    title = R.string.schedule_from_repository,
                    icon = R.drawable.ic_schedule_from_repo,
                    onItemClicked = onRepositoryClicked
                )
            )
            items.forEach { item ->
                CreateScheduleItem(
                    item = item,
                    modifier = Modifier
                        .weight(1f)
                        .padding(Dimen.ContentPadding),
                    contentPadding = PaddingValues(vertical = Dimen.ContentPadding)
                )
            }
        }
    }
}

@Composable
private fun CreateScheduleItem(
    item: ScheduleCreatorItem,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    iconSize: Dp = 64.dp,
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(RoundedCornerShape(Dimen.ContentPadding))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(bounded = true),
                onClick = item.onItemClicked
            )
            .padding(contentPadding)
    ) {
        Icon(
            painter = painterResource(item.icon),
            contentDescription = null,
            modifier = Modifier.size(iconSize),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = stringResource(item.title),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

private class ScheduleCreatorItem(
    @StringRes val title: Int,
    @DrawableRes val icon: Int,
    val onItemClicked: () -> Unit,
)