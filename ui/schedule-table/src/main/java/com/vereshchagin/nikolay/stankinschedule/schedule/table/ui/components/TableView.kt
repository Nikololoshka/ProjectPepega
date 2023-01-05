package com.vereshchagin.nikolay.stankinschedule.schedule.table.ui.components

import android.content.res.Configuration
import android.graphics.Paint
import android.text.TextPaint
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalConfiguration
import com.vereshchagin.nikolay.stankinschedule.schedule.table.domain.model.DrawScheduleTable
import com.vereshchagin.nikolay.stankinschedule.schedule.table.domain.model.TableConfig
import com.vereshchagin.nikolay.stankinschedule.schedule.table.domain.usecase.drawScheduleTable

@Composable
fun TableView(
    table: DrawScheduleTable,
    tableConfig: TableConfig,
    modifier: Modifier = Modifier,
    scale: Float = 1f,
) {
    val configuration = LocalConfiguration.current

    val textPaint = remember(tableConfig.color) {
        TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            color = tableConfig.color
        }
    }
    val linePaint = remember(tableConfig.color) {
        TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            color = tableConfig.color
            strokeWidth = 1f
        }
    }

    Canvas(
        modifier = modifier,
    ) {
        var width: Float
        var height: Float

        if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            width = size.width
            height = width / 1.4f // 1.4 - sqrt(2)
        } else {
            height = size.height
            width = height * 1.4f // 1.4 - sqrt(2)
        }

        width *= scale
        height *= scale

        drawIntoCanvas {
            it.nativeCanvas.apply {
                // смещение на центр
                translate(
                    size.width / 2f - width / 2f,
                    size.height / 2f - height / 2f
                )

                drawScheduleTable(
                    drawScheduleTable = table,
                    pageHeight = height,
                    pageWidth = width,
                    tableColor = tableConfig.color,
                    textPaint = textPaint,
                    linePaint = linePaint
                )
            }
        }
    }
}