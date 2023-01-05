package com.vereshchagin.nikolay.stankinschedule.schedule.table.domain.model

import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.DayOfWeek

class DrawScheduleTable(
    val scheduleName: String,
    private val days: Map<DayOfWeek, List<ScheduleTableCell>>,
    private val lines: List<Int>
) {
    operator fun get(day: DayOfWeek): List<ScheduleTableCell> {
        return days.getOrElse(day) { emptyList() }
    }

    fun linesPerDay(): List<Int> {
        return lines
    }
}