package com.vereshchagin.nikolay.stankinschedule.schedule.parser.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.vereshchagin.nikolay.stankinschedule.core.ui.theme.Dimen
import com.vereshchagin.nikolay.stankinschedule.schedule.parser.ui.R
import com.vereshchagin.nikolay.stankinschedule.schedule.parser.ui.model.ParserState

@Composable
fun StepperNavigation(
    parserState: ParserState,
    navigateBack: () -> Unit,
    navigateNext: () -> Unit,
    canNext: Boolean,
    stepCount: Int,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(Dimen.ContentPadding),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        OutlinedButton(
            onClick = navigateBack,
            enabled = true
        ) {
            Text(text = stringResource(R.string.step_back))
        }

        LineProgressStepper(
            step = parserState.step,
            count = stepCount,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = Dimen.ContentPadding)
        )

        Button(
            onClick = navigateNext,
            enabled = canNext,
        ) {
            Text(text = stringResource(R.string.step_next))
        }
    }
}