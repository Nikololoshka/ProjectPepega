package com.vereshchagin.nikolay.stankinschedule.home.ui.components.schedule

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.vereshchagin.nikolay.stankinschedule.core.ui.theme.Dimen
import com.vereshchagin.nikolay.stankinschedule.schedule.settings.domain.model.PairColorGroup
import com.vereshchagin.nikolay.stankinschedule.schedule.viewer.domain.model.ScheduleViewDay
import com.vereshchagin.nikolay.stankinschedule.schedule.viewer.ui.pair.PairColors
import com.vereshchagin.nikolay.stankinschedule.schedule.viewer.ui.pair.ScheduleDayCard
import com.vereshchagin.nikolay.stankinschedule.schedule.viewer.ui.pair.toColor

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ScheduleHome(
    days: List<ScheduleViewDay>,
    onLinkClicked: (url: String) -> Unit,
    modifier: Modifier = Modifier,
    colors: PairColors = PairColorGroup.default().toColor()
) {
    val pagerState = rememberPagerState((days.size - 1) / 2)
    val isScrolling = remember(pagerState) {
        derivedStateOf { pagerState.currentPageOffset != 0f }
    }

    Column(modifier = modifier) {

        SchedulePagerIndicator(
            state = pagerState,
            itemsCount = days.size,
            indicatorColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier.fillMaxWidth()
        )

        HorizontalPager(
            count = days.size,
            state = pagerState,
            verticalAlignment = Alignment.Top,
            modifier = Modifier.fillMaxWidth(),
        ) { page ->
            ScheduleDayCard(
                scheduleDay = days[page],
                pairColors = colors,
                onPairClicked = {},
                onLinkClicked = onLinkClicked,
                enabled = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimen.ContentPadding)
                    .wrapPagerHeight(page, pagerState, isScrolling.value),
            )
        }
    }
}


@OptIn(ExperimentalPagerApi::class)
@Composable
private fun Modifier.wrapPagerHeight(
    page: Int,
    pagerState: PagerState,
    isScrolling: Boolean
): Modifier {
    return if (pagerState.currentPage == page || isScrolling) {
        this.wrapContentHeight()
    } else {
        this.requiredHeightIn(max = 100.dp)
    }
}
