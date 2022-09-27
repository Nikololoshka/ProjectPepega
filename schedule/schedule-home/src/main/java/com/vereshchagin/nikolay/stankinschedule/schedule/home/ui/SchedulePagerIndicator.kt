package com.vereshchagin.nikolay.stankinschedule.schedule.home.ui

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState

@OptIn(ExperimentalPagerApi::class)
@Composable
fun SchedulePagerIndicator(
    state: PagerState,
    itemsCount: Int,
    modifier: Modifier = Modifier,
    indicatorHeight: Dp = 2.dp,
    indicatorColor: Color = Color.Magenta
) {
    val progress: Float by derivedStateOf { state.currentPage + state.currentPageOffset }

    Canvas(modifier = modifier) {
        val canvasWidth = size.width

        val delta = canvasWidth / itemsCount

        drawLine(
            color = indicatorColor,
            start = Offset(progress * delta, 0f),
            end = Offset(progress * delta + delta, 0f),
            strokeWidth = indicatorHeight.toPx()
        )
    }
}