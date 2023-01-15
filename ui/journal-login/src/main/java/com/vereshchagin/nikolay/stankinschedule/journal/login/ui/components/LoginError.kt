package com.vereshchagin.nikolay.stankinschedule.journal.login.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import com.vereshchagin.nikolay.stankinschedule.core.ui.components.LocalAnalytics
import com.vereshchagin.nikolay.stankinschedule.core.ui.theme.Dimen
import com.vereshchagin.nikolay.stankinschedule.core.ui.utils.exceptionDescription
import com.vereshchagin.nikolay.stankinschedule.journal.login.ui.R

@Composable
fun LoginError(
    error: Throwable,
    modifier: Modifier = Modifier,
) {
    val analytics = LocalAnalytics.current

    Row(
        horizontalArrangement = Arrangement.spacedBy(Dimen.ContentPadding),
        modifier = modifier
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_login_error),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.align(Alignment.CenterVertically)
        )
        Text(
            text = exceptionDescription(error).let { description ->
                if (description == null) {
                    analytics.recordException(error)
                    error.toString()
                } else {
                    description
                }
            },
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .weight(1f)
        )
    }
}