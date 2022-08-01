package com.vereshchagin.nikolay.stankinschedule.modulejournal.ui.login.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.vereshchagin.nikolay.stankinschedule.core.ui.theme.Dimen

@Composable
fun LoginError(
    error: String?,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(Dimen.ContentPadding),
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.Filled.Error,
            contentDescription = null,
            tint = MaterialTheme.colors.error,
            modifier = Modifier.align(Alignment.CenterVertically)
        )
        Text(
            text = error ?: "Unknown exception",
            color = MaterialTheme.colors.error,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .weight(1f)
        )
    }
}