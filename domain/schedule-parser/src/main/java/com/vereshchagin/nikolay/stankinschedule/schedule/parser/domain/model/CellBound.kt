package com.vereshchagin.nikolay.stankinschedule.schedule.parser.domain.model

class CellBound(
    val text: String,
    val x: Float,
    val y: Float,
    val h: Float,
    val w: Float,
    val maxFontHeight: Float
) {
    override fun toString(): String {
        return "CellBound(text='$text', x=$x, y=$y, h=$h, w=$w, maxFontHeight=$maxFontHeight)"
    }
}