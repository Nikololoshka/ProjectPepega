package com.vereshchagin.nikolay.stankinschedule.settings.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.ContentAlpha
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.vereshchagin.nikolay.stankinschedule.core.R as R_core


@Composable
fun <T : Any> DialogPreference(
    title: String,
    items: List<T>,
    selected: T,
    onItemChanged: (item: T) -> Unit,
    label: @Composable (item: T) -> String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    @DrawableRes icon: Int? = null,
) {
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        DialogList(
            title = title,
            items = items,
            selected = selected,
            label = label,
            onItemChanged = onItemChanged,
            onDismiss = { showDialog = false }
        )
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                enabled = enabled,
                onClick = { showDialog = true },
            )
            .padding(all = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        if (icon != null) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        }

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = if (!enabled) {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = ContentAlpha.disabled)
                } else {
                    Color.Unspecified
                },
            )
            Text(
                text = label(selected),
                style = MaterialTheme.typography.bodySmall,
                color = if (!enabled) {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = ContentAlpha.disabled)
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = ContentAlpha.medium)
                },
            )
        }
    }
}

@Composable
private fun <T : Any> DialogList(
    title: String,
    items: List<T>,
    selected: T,
    label: @Composable (item: T) -> String,
    onItemChanged: (item: T) -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(text = stringResource(R_core.string.ok))
            }
        },
        title = { Text(text = title) },
        text = {
            Column {
                items.forEach { item ->
                    RadioPreferenceItem(
                        title = label(item),
                        selected = selected == item,
                        onClick = {
                            onItemChanged(item)
                            onDismiss()
                        },
                    )
                }
            }
        }
    )
}

@Composable
private fun RadioPreferenceItem(
    title: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
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
    ) {

        RadioButton(
            selected = selected,
            onClick = onClick,
            interactionSource = interactionSource,
        )

        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .padding(start = 16.dp)
                .weight(weight = 1f),
        )
    }
}