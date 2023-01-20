package com.vereshchagin.nikolay.stankinschedule.schedule.table.domain.model

import android.graphics.Canvas
import android.graphics.Paint
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.text.TextUtils
import androidx.core.graphics.withRotation
import androidx.core.graphics.withTranslation

fun <R, P : Paint> P.withSize(fontSize: Float, draw: (paint: P) -> R): R {
    val prevSize = this.textSize
    this.textSize = fontSize
    val result = draw(this)
    this.textSize = prevSize
    return result
}

fun Paint.centerBaseline(): Float = (descent() + ascent()) / 2f

fun Paint.lineHeight(): Float = fontMetrics.run { descent - ascent }

fun fontSizeForHeight(height: Float, textPaint: Paint): Float {
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

fun prepareMultilineLayout(
    text: String,
    width: Int,
    height: Int,
    fontSize: Float,
    paint: TextPaint
): StaticLayout {
    var layout: StaticLayout

    val textPaint = TextPaint(paint)
    var currentFontSize = fontSize

    while (true) {
        textPaint.textSize = currentFontSize

        layout = StaticLayout.Builder.obtain(text, 0, text.length, textPaint, width)
            .setEllipsizedWidth(width)
            .setEllipsize(TextUtils.TruncateAt.END)
            .setHyphenationFrequency(Layout.HYPHENATION_FREQUENCY_FULL)
            .build()

        if (layout.height <= height || currentFontSize < 3f) {
            break
        }

        currentFontSize -= 0.5f
    }

    return layout
}

private fun Canvas.drawMultiText(
    text: String,
    x: Float,
    y: Float,
    w: Int,
    h: Int,
    fontSize: Float,
    textPaint: TextPaint,
) {
    var layout: StaticLayout

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

fun Canvas.drawCenterText(
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

fun Canvas.drawText(
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
