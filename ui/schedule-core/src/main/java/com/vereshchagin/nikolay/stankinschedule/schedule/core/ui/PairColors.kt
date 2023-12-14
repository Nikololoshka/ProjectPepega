package com.vereshchagin.nikolay.stankinschedule.schedule.core.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
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

@Composable
fun pairTextColor(
    background: Color,
    isDark: Boolean = background.luminance() < 0.5f
): Color {
    return if (isSystemInDarkTheme()) {
        if (isDark) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.surface
    } else {
        if (isDark) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.onSurface
    }
}