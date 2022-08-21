package com.vereshchagin.nikolay.stankinschedule.schedule.ui.viewer.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.vereshchagin.nikolay.stankinschedule.core.ui.theme.Dimen
import com.vereshchagin.nikolay.stankinschedule.schedule.R
import com.vereshchagin.nikolay.stankinschedule.schedule.domain.model.PairModel
import com.vereshchagin.nikolay.stankinschedule.schedule.domain.model.ScheduleViewDay
import java.util.*

@Composable
fun ScheduleDayCard(
    scheduleDay: ScheduleViewDay,
    pairColors: PairColors,
    onPairClicked: (pair: PairModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    Column(
        modifier = modifier
    ) {
        Text(
            text = scheduleDay.day.toString("EEEE, dd MMMM").toTitleCase(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimen.ContentPadding)
        )

        if (scheduleDay.pairs.isEmpty()) {

            Text(
                text = stringResource(R.string.schedule_no_pairs),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimen.ContentPadding)
            )
        }

        scheduleDay.pairs.forEach { pair ->
            PairCard(
                pair = pair,
                pairColors = pairColors,
                onClicked = { onPairClicked(pair) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimen.ContentPadding)
            )
        }
    }
}

private fun String.toTitleCase(locale: Locale = Locale.ROOT): String {
    return replaceFirstChar { if (it.isLowerCase()) it.titlecase(locale) else it.toString() }
}