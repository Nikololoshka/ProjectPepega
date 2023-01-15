package com.vereshchagin.nikolay.stankinschedule.schedule.editor.ui.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.DateItem
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.DateRange
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.DateSingle
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.Frequency
import com.vereshchagin.nikolay.stankinschedule.schedule.editor.ui.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateChip(
    item: DateItem,
    onClicked: () -> Unit,
    modifier: Modifier = Modifier,
    dateFormat: String = "dd.MM.yyyy",
) {
    val label = when (item) {
        is DateSingle -> {
            item.toString(dateFormat)
        }
        is DateRange -> {
            val frequency = when (item.frequency()) {
                Frequency.EVERY -> stringResource(R.string.frequency_every_week_simple)
                Frequency.THROUGHOUT -> stringResource(R.string.frequency_throughout_simple)
                else -> ""
            }

            item.toString(dateFormat, " - ") + " " + frequency
        }
    }

    SuggestionChip(
        onClick = onClicked,
        label = {
            Text(
                text = label
            )
        },
        modifier = modifier
    )
}