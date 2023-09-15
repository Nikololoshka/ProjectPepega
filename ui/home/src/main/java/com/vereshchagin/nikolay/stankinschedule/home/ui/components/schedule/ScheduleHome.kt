package com.vereshchagin.nikolay.stankinschedule.home.ui.components.schedule

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.vereshchagin.nikolay.stankinschedule.core.ui.theme.Dimen
import com.vereshchagin.nikolay.stankinschedule.home.ui.R
import com.vereshchagin.nikolay.stankinschedule.schedule.core.ui.PairColors
import com.vereshchagin.nikolay.stankinschedule.schedule.core.ui.ScheduleDayCard
import com.vereshchagin.nikolay.stankinschedule.schedule.core.ui.toColor
import com.vereshchagin.nikolay.stankinschedule.schedule.settings.domain.model.PairColorGroup
import com.vereshchagin.nikolay.stankinschedule.schedule.viewer.domain.model.ScheduleViewDay


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ScheduleHome(
    days: List<ScheduleViewDay>,
    onLinkClicked: (url: String) -> Unit,
    modifier: Modifier = Modifier,
    colors: PairColors = PairColorGroup.default().toColor()
) {
    val pagerState = rememberPagerState(
        initialPage = (days.size - 1) / 2,
        pageCount = { days.size }
    )
    val isScrolling = remember(pagerState) {
        derivedStateOf { pagerState.currentPageOffsetFraction != 0f }
    }


    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    Column(modifier = modifier) {

        SchedulePagerIndicator(
            state = pagerState,
            itemsCount = days.size,
            indicatorColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier.fillMaxWidth()
        )

        HorizontalPager(
            state = pagerState,
            verticalAlignment = Alignment.Top,
            modifier = Modifier.fillMaxWidth(),
        ) { page ->
            ScheduleDayCard(
                scheduleDay = days[page],
                pairColors = colors,
                onPairClicked = {},
                onLinkClicked = onLinkClicked,
                onLinkCopied = {
                    clipboardManager.setText(AnnotatedString((it)))
                    Toast.makeText(context, R.string.link_copied, Toast.LENGTH_SHORT).show()
                },
                enabled = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimen.ContentPadding)
                    .wrapPagerHeight(page, pagerState, isScrolling.value),
            )
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
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
