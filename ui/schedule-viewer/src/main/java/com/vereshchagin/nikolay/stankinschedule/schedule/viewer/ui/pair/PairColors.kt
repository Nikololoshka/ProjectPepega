package com.vereshchagin.nikolay.stankinschedule.schedule.viewer.ui.pair

import androidx.compose.ui.graphics.Color
import com.vereshchagin.nikolay.stankinschedule.core.ui.ext.parse
import com.vereshchagin.nikolay.stankinschedule.schedule.settings.domain.model.PairColorGroup

class PairColors(
    val lectureColor: Color,
    val seminarColor: Color,
    val laboratoryColor: Color,
    val subgroupAColor: Color,
    val subgroupBColor: Color
)

fun PairColorGroup.toColor(): PairColors {
    return PairColors(
        lectureColor = Color.parse(lectureColor),
        seminarColor = Color.parse(seminarColor),
        laboratoryColor = Color.parse(laboratoryColor),
        subgroupAColor = Color.parse(subgroupAColor),
        subgroupBColor = Color.parse(subgroupBColor)
    )
}