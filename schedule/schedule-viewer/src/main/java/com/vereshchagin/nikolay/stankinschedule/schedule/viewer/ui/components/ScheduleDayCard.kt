package com.vereshchagin.nikolay.stankinschedule.schedule.viewer.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.vereshchagin.nikolay.stankinschedule.core.ui.theme.Dimen
import com.vereshchagin.nikolay.stankinschedule.core.ui.toTitleCase
import com.vereshchagin.nikolay.stankinschedule.schedule.viewer.R
import com.vereshchagin.nikolay.stankinschedule.schedule.viewer.domain.model.ScheduleViewDay
import com.vereshchagin.nikolay.stankinschedule.schedule.viewer.domain.model.ScheduleViewPair

@Composable
fun ScheduleDayCard(
    scheduleDay: ScheduleViewDay,
    pairColors: PairColors,
    onPairClicked: (pair: ScheduleViewPair) -> Unit,
    modifier: Modifier = Modifier,
) {

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