package com.vereshchagin.nikolay.stankinschedule.home.ui.components.schedule

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SchedulePagerIndicator(
    state: PagerState,
    itemsCount: Int,
    modifier: Modifier = Modifier,
    indicatorHeight: Dp = 2.dp,
    indicatorColor: Color = Color.Magenta
) {
    val progress: Float by remember {
        derivedStateOf { state.currentPage + state.currentPageOffsetFraction }
    }

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