package com.vereshchagin.nikolay.stankinschedule.schedule.parser.ui.forms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.vereshchagin.nikolay.stankinschedule.core.ui.components.Stateful
import com.vereshchagin.nikolay.stankinschedule.core.ui.theme.Dimen
import com.vereshchagin.nikolay.stankinschedule.schedule.parser.ui.R
import com.vereshchagin.nikolay.stankinschedule.schedule.parser.ui.model.ParserState

@Composable
fun FinishForm(
    state: ParserState.ImportFinish,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Stateful(
            state = state.state,
            onSuccess = {
                Text(
                    text = stringResource(R.string.successfully_imported),
                    style = MaterialTheme.typography.titleMedium
                )
                Icon(
                    painter = painterResource(R.drawable.ic_done_outline),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(64.dp)
                        .padding(Dimen.ContentPadding)
                )
            },
            onLoading = {
                CircularProgressIndicator()
            },
            onFailed = {
                Text(
                    text = it.toString(),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        )
    }
}