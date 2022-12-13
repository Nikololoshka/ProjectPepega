package com.vereshchagin.nikolay.stankinschedule.settings.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PreferenceDivider() {
    Divider(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    )
}