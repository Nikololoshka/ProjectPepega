package com.vereshchagin.nikolay.stankinschedule.schedule.table.ui.components

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize


@Composable
fun ZoomableBox(
    modifier: Modifier = Modifier,
    minScale: Float = 0.1f,
    maxScale: Float = 5f,
    onTap: (() -> Unit)? = null,
    content: @Composable BoxScope.(scale: Float, offsetX: Float, offsetY: Float) -> Unit
) {
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    var size by remember { mutableStateOf(IntSize.Zero) }
    var scale by remember { mutableStateOf(1f) }

    val processOffsets: (panX: Float, panY: Float) -> Unit = { panX, panY ->
        val maxX = (size.width * (scale - 1)) / 2
        val minX = -maxX

        offsetX = maxOf(minX, minOf(maxX, offsetX + panX))
        val maxY = (size.height * (scale - 1)) / 2
        val minY = -maxY
        offsetY = maxOf(minY, minOf(maxY, offsetY + panY))
    }

    Box(
        modifier = modifier
            .clip(RectangleShape)
            .onSizeChanged { size = it }
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scale = maxOf(minScale, minOf(scale * zoom, maxScale))
                    processOffsets(pan.x, pan.y)
                }
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onTap?.invoke() },
                    onDoubleTap = {
                        scale = if (scale == minScale) {
                            maxScale / 2
                        } else {
                            minScale
                        }
                        processOffsets(0f, 0f)
                    }
                )
            }
    ) {
        this.content(scale, offsetX, offsetY)
    }
}