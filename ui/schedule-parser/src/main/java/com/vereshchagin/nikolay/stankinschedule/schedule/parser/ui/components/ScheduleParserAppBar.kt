package com.vereshchagin.nikolay.stankinschedule.schedule.parser.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.vereshchagin.nikolay.stankinschedule.core.ui.components.BackButton
import com.vereshchagin.nikolay.stankinschedule.schedule.parser.ui.R
import com.vereshchagin.nikolay.stankinschedule.schedule.parser.ui.model.ParserState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleParserAppBar(
    state: ParserState,
    onBackPressed: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = stringResource(R.string.parser_title),
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = when (state.step) {
                        1 -> stringResource(R.string.step_1)
                        2 -> stringResource(R.string.step_2)
                        3 -> stringResource(R.string.step_3)
                        4 -> stringResource(R.string.step_4)
                        5 -> stringResource(R.string.step_5)
                        else -> ""
                    },
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        },
        actions = {
            CircleProgressStepper(
                step = state.step,
                count = ParserState.STEP_TOTAL,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .size(32.dp)
            )
        },
        navigationIcon = { BackButton(onClick = onBackPressed) },
        scrollBehavior = scrollBehavior,
        modifier = modifier
    )
}