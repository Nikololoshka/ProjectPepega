package com.vereshchagin.nikolay.stankinschedule.schedule.list.ui

import android.content.Intent
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.vereshchagin.nikolay.stankinschedule.core.ui.theme.Dimen
import com.vereshchagin.nikolay.stankinschedule.schedule.list.R
import com.vereshchagin.nikolay.stankinschedule.schedule.list.ui.components.CreateState
import com.vereshchagin.nikolay.stankinschedule.schedule.list.ui.components.ScheduleCreateDialog
import java.io.File


@Composable
fun ScheduleCreatorSheet(
    onNavigateBack: () -> Unit,
    onRepositoryClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {

    var createDialog by remember { mutableStateOf<CreateState?>(null) }

    createDialog?.let {
        ScheduleCreateDialog(
            state = it,
            onDismiss = {
                createDialog = null
                onNavigateBack()
            },
            onCreate = {

            }
        )
    }

    val openScheduleLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = {
            if (it != null) {
                Log.d("ScheduleCreatorSheet", File(it.toString()).readText())
            }
        }
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(Dimen.ContentPadding),
        modifier = modifier.padding(
            vertical = Dimen.ContentPadding * 2,
            horizontal = Dimen.ContentPadding
        )
    ) {
        Text(
            text = stringResource(R.string.schedule_creator),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = Dimen.ContentPadding * 2)
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            val context = LocalContext.current

            val items = listOf(
                ScheduleCreatorItem(
                    title = R.string.schedule_create_new,
                    icon = R.drawable.ic_schedule_new,
                    onItemClicked = { createDialog = CreateState.New }
                ),
                ScheduleCreatorItem(
                    title = R.string.schedule_from_device,
                    icon = R.drawable.ic_schedule_from_device,
                    onItemClicked = { openScheduleLauncher.launch(arrayOf("application/json")) }
                ),
                ScheduleCreatorItem(
                    title = R.string.schedule_from_repository,
                    icon = R.drawable.ic_schedule_from_repo,
                    onItemClicked = onRepositoryClicked
                )
            )
            items.forEach { item ->
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1f)
                        .clip(CircleShape)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = rememberRipple(bounded = true),
                            onClick = item.onItemClicked
                        )
                ) {
                    Icon(
                        painter = painterResource(item.icon),
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = stringResource(item.title),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

private class ScheduleCreatorItem(
    @StringRes val title: Int,
    @DrawableRes val icon: Int,
    val onItemClicked: () -> Unit,
)