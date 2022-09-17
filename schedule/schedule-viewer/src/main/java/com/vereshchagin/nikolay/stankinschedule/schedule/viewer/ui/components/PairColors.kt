package com.vereshchagin.nikolay.stankinschedule.schedule.viewer.ui.components

import androidx.compose.ui.graphics.Color

class PairColors(
    val lectureColor: Color,
    val seminarColor: Color,
    val laboratoryColor: Color,
    val subgroupAColor: Color,
    val subgroupBColor: Color
) {
    companion object {

        private fun Color.Companion.parse(hex: String): Color {
            return Color(android.graphics.Color.parseColor(hex))
        }

        fun defaults() = PairColors(
            lectureColor = Color.parse("#80DEEA"),
            seminarColor = Color.parse("#FFF59D"),
            laboratoryColor = Color.parse("#C5E1A5"),
            subgroupAColor = Color.parse("#FFCC80"),
            subgroupBColor = Color.parse("#D1C4E9")
        )
    }
}