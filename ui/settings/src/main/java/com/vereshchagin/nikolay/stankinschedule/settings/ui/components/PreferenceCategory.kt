package com.vereshchagin.nikolay.stankinschedule.settings.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun PreferenceCategory(
    title: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = title,
        modifier = modifier
            .padding(
                start = 16.dp,
                top = 16.dp,
                end = 8.dp,
                bottom = 8.dp,
            ),
        color = MaterialTheme.colorScheme.primary,
        style = MaterialTheme.typography.titleSmall,
    )
}

@Preview(showBackground = true)
@Composable
private fun PreferenceCategoryPreview() {
    PreferenceCategory(title = "Miscellaneous")
}