package com.vereshchagin.nikolay.stankinschedule.schedule.table.domain.model

import android.text.StaticLayout
import android.text.TextPaint

class DrawScheduleTableCell(
    cell: ScheduleTableCell,
    width: Int,
    height: Int,
    fontSize: Float,
    textPaint: TextPaint
) {
    val row: Int = cell.row
    val column: Int = cell.column
    val rowSpan: Int = cell.rowSpan
    val columnSpan: Int = cell.columnSpan

    var layout: StaticLayout? = null
        private set

    init {
        if (cell.text.isNotEmpty()) {
            layout = prepareMultilineLayout(
                text = cell.text,
                width = width,
                height = height,
                fontSize = fontSize,
                paint = textPaint
            )
        }
    }
}