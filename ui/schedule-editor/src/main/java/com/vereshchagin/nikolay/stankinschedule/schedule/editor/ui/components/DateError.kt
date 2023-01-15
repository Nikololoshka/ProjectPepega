package com.vereshchagin.nikolay.stankinschedule.schedule.editor.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import com.vereshchagin.nikolay.stankinschedule.core.ui.R
import com.vereshchagin.nikolay.stankinschedule.core.ui.theme.Dimen

@Composable
fun DateError(
    error: String,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(Dimen.ContentPadding),
        modifier = modifier
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_error),
            contentDescription = null,
            tint = MaterialTheme.colors.error,
            modifier = Modifier.align(Alignment.CenterVertically)
        )
        Text(
            text = error,
            color = MaterialTheme.colors.error,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .weight(1f)
        )
    }
}