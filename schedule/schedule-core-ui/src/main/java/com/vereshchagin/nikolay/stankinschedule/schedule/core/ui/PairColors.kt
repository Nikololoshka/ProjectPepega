package com.vereshchagin.nikolay.stankinschedule.schedule.core.ui

import androidx.compose.ui.graphics.Color
import com.vereshchagin.nikolay.stankinschedule.schedule.core.ui.data.toColor
import com.vereshchagin.nikolay.stankinschedule.schedule.settings.domain.model.PairColorGroup

class PairColors(
    val lectureColor: Color,
    val seminarColor: Color,
    val laboratoryColor: Color,
    val subgroupAColor: Color,
    val subgroupBColor: Color
) {
    companion object {
        fun defaults() = PairColorGroup.default().toColor()
    }
}
