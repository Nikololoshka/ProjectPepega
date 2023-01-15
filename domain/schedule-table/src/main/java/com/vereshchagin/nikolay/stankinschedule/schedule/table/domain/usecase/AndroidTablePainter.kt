package com.vereshchagin.nikolay.stankinschedule.schedule.table.domain.usecase

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.text.TextUtils
import androidx.core.graphics.withRotation
import androidx.core.graphics.withTranslation
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.DayOfWeek
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.Time
import com.vereshchagin.nikolay.stankinschedule.schedule.table.domain.model.DrawScheduleTable

fun Canvas.drawScheduleTable(
    drawScheduleTable: DrawScheduleTable,
    pageHeight: Float,
    pageWidth: Float,
    tableColor: Int = Color.BLACK,
    textPaint: TextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = tableColor
    },
    linePaint: TextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = tableColor
        strokeWidth = 1f
    },
    scale: Float = pageWidth / 1920f,
    pagePadding: Float = pageHeight * 0.05f
) {
    val ROW_COUNT = 6
    val COLUMN_COUNT = 8

    val TITLE_SIZE = 28f * scale
    val HEADER_SIZE = 32f * scale
    val TEXT_SIZE = 14f * scale
    val TEXT_PADDING = 4 * scale

    val drawWidth = pageWidth - 2 * pagePadding
    val drawHeight = pageHeight - 2 * pagePadding

    var x = pagePadding
    var y = pagePadding

    // Schedule title
    val titleHeight = drawText(
        text = drawScheduleTable.scheduleName,
        x = x + drawWidth / 2,
        y = y,
        fontSize = TITLE_SIZE,
        paint = textPaint
    )
    val titleBottomPadding = pagePadding / 8
    y += titleHeight + titleBottomPadding

    // Schedule headers
    drawRect(x, y, x + HEADER_SIZE, y + HEADER_SIZE, linePaint)

    val headerFontSize = fontSizeForHeight(HEADER_SIZE - 2 * TEXT_PADDING, textPaint)

    // Rows
    val rowHeight = (drawHeight - HEADER_SIZE - titleHeight - titleBottomPadding) / ROW_COUNT
    val daysOfWeek = listOf("Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота")

    for (i in 0 until ROW_COUNT) {
        val cellX = x
        val cellY = y + HEADER_SIZE + i * rowHeight

        drawRect(cellX, cellY, cellX + HEADER_SIZE, cellY + rowHeight, linePaint)
        drawCenterText(
            text = daysOfWeek[i],
            x = cellX,
            y = cellY,
            w = HEADER_SIZE,
            h = rowHeight,
            fontSize = headerFontSize,
            rotate = -90,
            paint = textPaint
        )
    }

    // Columns
    val columnWidth = (drawWidth - HEADER_SIZE) / COLUMN_COUNT
    val times = Time.STARTS.zip(Time.ENDS) { a, b -> "$a - $b" }

    for (i in 0 until COLUMN_COUNT) {
        val cellX = x + HEADER_SIZE + i * columnWidth
        val cellY = y

        drawRect(cellX, cellY, cellX + columnWidth, cellY + HEADER_SIZE, linePaint)
        drawCenterText(
            text = times[i],
            x = cellX,
            y = cellY,
            w = columnWidth,
            h = HEADER_SIZE,
            fontSize = headerFontSize,
            paint = textPaint
        )
    }

    for ((index, rowCount) in drawScheduleTable.linesPerDay().withIndex()) {

        val cells = drawScheduleTable[DayOfWeek.values()[index]]
        val subRowHeight = rowHeight / rowCount

        for (cell in cells) {
            val cellX = x + HEADER_SIZE + cell.column * columnWidth
            val cellY = y + HEADER_SIZE + index * rowHeight + cell.row * subRowHeight
            val cellW = cellX + columnWidth * cell.columnSpan
            val cellH = cellY + subRowHeight * cell.rowSpan

            drawRect(cellX, cellY, cellW, cellH, linePaint)
            drawMultiText(
                text = cell.text,
                x = cellX + TEXT_PADDING,
                y = cellY + TEXT_PADDING,
                w = (columnWidth * cell.columnSpan - 2 * TEXT_PADDING).toInt(),
                h = (subRowHeight * cell.rowSpan - 2 * TEXT_PADDING).toInt(),
                fontSize = TEXT_SIZE,
                paint = textPaint
            )
        }
    }
}

private fun Canvas.drawMultiText(
    text: String,
    x: Float,
    y: Float,
    w: Int,
    h: Int,
    fontSize: Float,
    paint: TextPaint,
) {
    if (w < 0) {
        return
    }

    var layout: StaticLayout

    val textPaint = paint
    var currentFontSize = fontSize

    while (true) {
        textPaint.textSize = currentFontSize

        layout = StaticLayout.Builder.obtain(text, 0, text.length, textPaint, w)
            .setEllipsizedWidth(w)
            .setEllipsize(TextUtils.TruncateAt.END)
            .setHyphenationFrequency(Layout.HYPHENATION_FREQUENCY_FULL)
            .build()

        if (layout.height <= h || currentFontSize < 3f) {
            break
        }

        currentFontSize -= 0.5f
    }


    withTranslation(x, y + textPaint.centerBaseline()) {
        layout.draw(this)
    }
}

private fun Canvas.drawCenterText(
    text: String,
    x: Float,
    y: Float,
    w: Float,
    h: Float,
    fontSize: Float,
    paint: Paint,
    rotate: Int = 0
) = paint.withSize(fontSize) { textPaint ->
    val textWidth = paint.measureText(text) / 2
    val centerX = x + w / 2 - textWidth
    val centerY = y + h / 2 - textPaint.centerBaseline()

    if (rotate == 0) {
        drawText(text, centerX, centerY, textPaint)
    } else {
        withRotation(
            degrees = rotate.toFloat(),
            pivotX = x + w / 2,
            pivotY = y + h / 2
        ) {
            drawText(text, centerX, centerY, textPaint)
        }
    }
}

private fun Canvas.drawText(
    text: String,
    x: Float,
    y: Float,
    fontSize: Float,
    paint: Paint
): Float = paint.withSize(fontSize) { textPaint ->
    val textWidth = paint.measureText(text)
    drawText(text, x - textWidth / 2, y - textPaint.centerBaseline(), textPaint)
    textPaint.lineHeight()
}

private fun <R, P : Paint> P.withSize(fontSize: Float, draw: (paint: P) -> R): R {
    val prevSize = this.textSize
    this.textSize = fontSize
    val result = draw(this)
    this.textSize = prevSize
    return result
}

private fun Paint.centerBaseline(): Float = (descent() + ascent()) / 2f

private fun Paint.lineHeight(): Float = fontMetrics.run { descent - ascent }

private fun fontSizeForHeight(height: Float, textPaint: Paint): Float {
    var fontSize = 1f
    while (true) {
        textPaint.textSize = fontSize + 1f
        val extra = textPaint.fontMetrics.run { descent - ascent }

        if (extra >= height) {
            break
        }

        fontSize += 1f
    }

    return fontSize
}