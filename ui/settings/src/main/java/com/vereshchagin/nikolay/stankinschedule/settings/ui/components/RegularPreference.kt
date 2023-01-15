package com.vereshchagin.nikolay.stankinschedule.settings.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.ContentAlpha
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Composable
fun RegularPreference(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    @DrawableRes icon: Int? = null,
) {
    RegularPreference(
        title = title,
        subtitle = AnnotatedString(text = subtitle),
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        icon = icon,
    )
}

@Composable
fun RegularPreference(
    title: String,
    subtitle: AnnotatedString,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    @DrawableRes icon: Int? = null,
) {

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                enabled = enabled,
                onClick = onClick,
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
    }
}

@Preview(showBackground = true)
@Composable
private fun RegularPreferencePreview() {
    RegularPreference(
        title = "Advanced settings",
        subtitle = AnnotatedString(text = "Lorem ipsum dolor sit amet"),
        onClick = { },
        // icon = R.drawable.ic_pref_general
    )
}