package com.vereshchagin.nikolay.stankinschedule.journal.viewer.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.vereshchagin.nikolay.stankinschedule.core.ui.components.LocalAnalytics
import com.vereshchagin.nikolay.stankinschedule.core.ui.utils.exceptionDescription
import com.vereshchagin.nikolay.stankinschedule.journal.viewer.ui.R

@Composable
fun JournalError(
    error: Throwable,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val analytics = LocalAnalytics.current

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = exceptionDescription(error).let { description ->
                if (description == null) {
                    analytics.recordException(error)
                    error.toString()
                } else {
                    description
                }
            },
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedButton(
            onClick = onRetry,
        ) {
            Text(
                text = stringResource(R.string.journal_retry)
            )
        }
    }
}