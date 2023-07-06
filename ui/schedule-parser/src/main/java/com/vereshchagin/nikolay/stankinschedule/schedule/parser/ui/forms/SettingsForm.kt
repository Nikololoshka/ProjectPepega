package com.vereshchagin.nikolay.stankinschedule.schedule.parser.ui.forms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.vereshchagin.nikolay.stankinschedule.core.ui.components.OutlinedSelectField
import com.vereshchagin.nikolay.stankinschedule.core.ui.theme.Dimen
import com.vereshchagin.nikolay.stankinschedule.schedule.parser.domain.model.ParserSettings
import com.vereshchagin.nikolay.stankinschedule.schedule.parser.ui.R
import com.vereshchagin.nikolay.stankinschedule.schedule.parser.ui.model.ParserState
import org.joda.time.LocalDate

@Composable
fun SettingsForm(
    state: ParserState.Settings,
    onSetupSettings: (settings: ParserSettings) -> Unit,
    modifier: Modifier = Modifier,
    scheduleYearVariants: List<Int> = MutableList(5) { LocalDate.now().year - 2 + it },
    parserThresholdVariants: List<Float> = MutableList(8) { 0.25f + 0.25f * it }
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimen.ContentPadding),
        modifier = modifier
    ) {
        OutlinedSelectField(
            value = state.settings.scheduleYear,
            onValueChanged = { scheduleYear ->
                onSetupSettings(state.settings.copy(scheduleYear = scheduleYear))
            },
            items = scheduleYearVariants,
            label = { Text(text = stringResource(R.string.settings_year)) },
            menuLabel = { it.toString() },
            supportingText = { Text(text = stringResource(R.string.settings_year_details)) },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedSelectField(
            value = state.settings.parserThreshold,
            onValueChanged = { parserThreshold ->
                onSetupSettings(state.settings.copy(parserThreshold = parserThreshold))
            },
            items = parserThresholdVariants,
            label = { Text(text = stringResource(R.string.settings_threshold)) },
            menuLabel = { it.toString() },
            supportingText = { Text(text = stringResource(R.string.settings_threshold_details)) },
            modifier = Modifier.fillMaxWidth()
        )
    }
}