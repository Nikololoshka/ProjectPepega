package com.vereshchagin.nikolay.stankinschedule.schedule.table.domain.usecase

import android.graphics.Canvas
import androidx.core.graphics.withTranslation
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.DayOfWeek
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.Time
import com.vereshchagin.nikolay.stankinschedule.schedule.table.domain.model.DrawScheduleTable
import com.vereshchagin.nikolay.stankinschedule.schedule.table.domain.model.drawCenterText
import com.vereshchagin.nikolay.stankinschedule.schedule.table.domain.model.drawText
import com.vereshchagin.nikolay.stankinschedule.schedule.table.domain.model.fontSizeForHeight

fun Canvas.drawScheduleTable(
    drawScheduleTable: DrawScheduleTable,
) {
    var x = drawScheduleTable.pagePadding
    var y = drawScheduleTable.pagePadding

    // Schedule title
    val titleHeight = drawText(
        text = drawScheduleTable.scheduleName,
        x = x + drawScheduleTable.drawWidth / 2,
        y = y,
        fontSize = drawScheduleTable.titleSize,
        paint = drawScheduleTable.textPaint
    )

    y += titleHeight + drawScheduleTable.titleBottomPadding

    // Schedule headers
    drawRect(
        x,
        y,
        x + drawScheduleTable.headerSize,
        y + drawScheduleTable.headerSize,
        drawScheduleTable.linePaint
    )

    val headerFontSize = fontSizeForHeight(
        drawScheduleTable.headerSize - 2 * drawScheduleTable.textPadding,
        drawScheduleTable.textPaint
    )

    // Rows
    val rowHeight = drawScheduleTable.rowHeight
    val daysOfWeek = listOf("Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота")

    for (i in 0 until DrawScheduleTable.ROW_COUNT) {
        val cellX = x
        val cellY = y + drawScheduleTable.headerSize + i * rowHeight

        drawRect(
            cellX,
            cellY,
            cellX + drawScheduleTable.headerSize,
            cellY + rowHeight,
            drawScheduleTable.linePaint
        )
        drawCenterText(
            text = daysOfWeek[i],
            x = cellX,
            y = cellY,
            w = drawScheduleTable.headerSize,
            h = rowHeight,
            fontSize = headerFontSize,
            rotate = -90,
            paint = drawScheduleTable.textPaint
        )
    }

    // Columns
    val columnWidth = drawScheduleTable.columnWidth
    val times = Time.STARTS.zip(Time.ENDS) { a, b -> "$a - $b" }

    for (i in 0 until DrawScheduleTable.COLUMN_COUNT) {
        val cellX = x + drawScheduleTable.headerSize + i * columnWidth
        val cellY = y

        drawRect(
            cellX,
            cellY,
            cellX + columnWidth,
            cellY + drawScheduleTable.headerSize,
            drawScheduleTable.linePaint
        )
        drawCenterText(
            text = times[i],
            x = cellX,
            y = cellY,
            w = columnWidth,
            h = drawScheduleTable.headerSize,
            fontSize = headerFontSize,
            paint = drawScheduleTable.textPaint
        )
    }

    for ((index, rowCount) in drawScheduleTable.lines.withIndex()) {

        val cells = drawScheduleTable[DayOfWeek.values()[index]]
        val subRowHeight = rowHeight / rowCount

        for (cell in cells) {
            val cellX = x + drawScheduleTable.headerSize + cell.column * columnWidth
            val cellY =
                y + drawScheduleTable.headerSize + index * rowHeight + cell.row * subRowHeight
            val cellW = cellX + columnWidth * cell.columnSpan
            val cellH = cellY + subRowHeight * cell.rowSpan

            drawRect(cellX, cellY, cellW, cellH, drawScheduleTable.linePaint)
            cell.layout?.let { layout ->
                withTranslation(
                    x = cellX + drawScheduleTable.textPadding,
                    y = cellY + drawScheduleTable.textPadding,
                ) {
                    layout.draw(this)
                }
            }
        }
    }
}