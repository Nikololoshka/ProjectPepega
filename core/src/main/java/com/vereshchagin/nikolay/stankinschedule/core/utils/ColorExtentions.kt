package com.vereshchagin.nikolay.stankinschedule.core.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb


fun Color.Companion.parse(hex: String): Color {
    return Color(android.graphics.Color.parseColor(hex))
}

fun Color.toHEX(): String {
    return String.format("#%06X", 0xFFFFFF and this.toArgb())
}