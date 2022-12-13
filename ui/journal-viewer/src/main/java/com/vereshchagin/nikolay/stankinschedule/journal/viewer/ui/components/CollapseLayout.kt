package com.vereshchagin.nikolay.stankinschedule.journal.viewer.ui.components

import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp

enum class SwipingStates {
    EXPANDED,
    COLLAPSED
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CollapseLayout(
    headerHeight: Dp,
    header: @Composable ColumnScope.(progress: Float) -> Unit,
    content: @Composable ColumnScope.() -> Unit,
    modifier: Modifier = Modifier,
) {
    val swipingState = rememberSwipeableState(
        initialValue = SwipingStates.EXPANDED,
        animationSpec = tween(durationMillis = 300)
    )

    val nestedScroll = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(
                available: Offset,
                source: NestedScrollSource,
            ): Offset {
                val delta = available.y
                return if (delta < 0) {
                    swipingState.performDrag(delta).toOffset()
                } else {
                    Offset.Zero
                }
            }

            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource,
            ): Offset {
                val delta = available.y
                return swipingState.performDrag(delta).toOffset()
            }

            override suspend fun onPreFling(available: Velocity): Velocity {
                return if (available.y < 0) {
                    swipingState.performFling(available.y)
                    available
                } else {
                    Velocity.Zero
                }
            }

            override suspend fun onPostFling(
                consumed: Velocity,
                available: Velocity,
            ): Velocity {
                swipingState.performFling(velocity = available.y)
                return super.onPostFling(consumed, available)
            }

            private fun Float.toOffset() = Offset(0f, this)
        }
    }

    val progress by derivedStateOf {
        if (swipingState.progress.to == SwipingStates.COLLAPSED) {
            swipingState.progress.fraction
        } else {
            1f - swipingState.progress.fraction
        }
    }

    val heightInPx = with(LocalDensity.current) { headerHeight.toPx() }

    Column(
        modifier = modifier
            .swipeable(
                state = swipingState,
                thresholds = { _, _ -> FractionalThreshold(0.5f) },
                orientation = Orientation.Vertical,
                anchors = mapOf(
                    // Maps anchor points (in px) to states
                    0f to SwipingStates.COLLAPSED,
                    heightInPx to SwipingStates.EXPANDED,
                ),
                velocityThreshold = 0.5.dp
            )
            .nestedScroll(nestedScroll)
    ) {
        header(progress)
        content()
    }
}