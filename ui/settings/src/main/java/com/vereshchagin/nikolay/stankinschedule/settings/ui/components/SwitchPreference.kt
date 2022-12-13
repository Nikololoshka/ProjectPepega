package com.vereshchagin.nikolay.stankinschedule.settings.ui.components

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ContentAlpha
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun SwitchPreference(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    SwitchPreference(
        title = title,
        subtitle = AnnotatedString(text = subtitle),
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
        enabled = enabled,
    )
}

@Composable
fun SwitchPreference(
    title: String,
    subtitle: AnnotatedString,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val interactionSource = remember { MutableInteractionSource() }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                enabled = enabled,
                onClick = { onCheckedChange(!checked) },
            )
            .padding(all = 16.dp),
    ) {
        Column(
            modifier = Modifier.weight(weight = 1f, fill = true),
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

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            interactionSource = interactionSource,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SwitchPreferencePreview() {

    var checked by remember { mutableStateOf(value = true) }

    SwitchPreference(
        title = "Dark theme",
        subtitle = AnnotatedString(text = "Lorem ipsum dolor sit amet"),
        checked = checked,
        onCheckedChange = { checked = it },
    )
}