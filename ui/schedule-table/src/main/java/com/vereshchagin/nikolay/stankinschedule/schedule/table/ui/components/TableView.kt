package com.vereshchagin.nikolay.stankinschedule.schedule.table.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import com.vereshchagin.nikolay.stankinschedule.schedule.table.domain.model.ScheduleTable
import com.vereshchagin.nikolay.stankinschedule.schedule.table.domain.model.TableConfig
import com.vereshchagin.nikolay.stankinschedule.schedule.table.domain.model.toDraw
import com.vereshchagin.nikolay.stankinschedule.schedule.table.domain.usecase.drawScheduleTable

@Composable
fun TableView(
    table: ScheduleTable,
    tableConfig: TableConfig,
    modifier: Modifier = Modifier,
) {
    val configuration = LocalConfiguration.current

    var width by remember { mutableStateOf(0f) }
    var height by remember { mutableStateOf(0f) }
    var scale by remember { mutableStateOf(1f) }

    val drawTable by remember(table, tableConfig.color) {
        derivedStateOf { table.toDraw(height, width, tableConfig.color) }
    }

    Canvas(
        modifier = modifier
            .onSizeChanged { size ->
                if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                    scale = 1.5f
                    width = size.width.toFloat() * scale
                    height = size.width * scale / 1.4f // 1.4 - sqrt(2)
                } else {
                    scale = 1f
                    height = size.height.toFloat()
                    width = size.height * 1.4f // 1.4 - sqrt(2)
                }
            },
    ) {

        drawIntoCanvas {
            it.nativeCanvas.apply {
                // увеличение для нормального отображения шрифта при увеличении
                scale(
                    1f / scale,
                    1f / scale,
                    size.width / 2f,
                    size.height / 2f
                )

                // смещение на центр
                translate(
                    size.width / 2f - width / 2f,
                    size.height / 2f - height / 2f
                )

                drawScheduleTable(drawTable)
            }
        }
    }
}