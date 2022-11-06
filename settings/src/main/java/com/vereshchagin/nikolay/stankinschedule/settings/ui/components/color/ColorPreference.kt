package com.vereshchagin.nikolay.stankinschedule.settings.ui.components.color

import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.ContentAlpha
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp


@Composable
fun ColorPreference(
    title: String,
    subtitle: String,
    color: Color,
    defaultColor: Color,
    onColorChanged: (color: Color) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    @DrawableRes icon: Int? = null,
    colorBorder: Color = MaterialTheme.colorScheme.onSurface
) {
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        ColorPickerDialog(
            title = title,
            color = color,
            onColorChanged = {
                onColorChanged(it)
                showDialog = false
            },
            onDefault = {
                onColorChanged(defaultColor)
                showDialog = false
            },
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
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = if (!enabled) {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = ContentAlpha.disabled)
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = ContentAlpha.medium)
                },
            )
        }

        ColorIcon(
            color = color,
            colorBorder = colorBorder
        )
    }
}
