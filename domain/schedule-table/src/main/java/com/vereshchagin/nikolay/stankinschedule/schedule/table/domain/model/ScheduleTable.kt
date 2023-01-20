package com.vereshchagin.nikolay.stankinschedule.schedule.table.domain.model

import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.DayOfWeek
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.ScheduleModel
import org.joda.time.LocalDate

class ScheduleTable {

    val days: Map<DayOfWeek, ScheduleTableDay> = buildMap(
        capacity = DayOfWeek.values().size,
        builderAction = {
            DayOfWeek.values().forEach { day ->
                this[day] = ScheduleTableDay()
            }
        }
    )

    val scheduleName: String
    val mode: TableMode

    constructor(schedule: ScheduleModel) {
        DayOfWeek.values().forEach { day ->
            days[day]?.setPairs(schedule.pairsByDay(day))
        }

        scheduleName = schedule.info.scheduleName
        mode = TableMode.Full
    }

    constructor(schedule: ScheduleModel, date: LocalDate) {
        var currentDate = date.withDayOfWeek(1)

        DayOfWeek.values().forEach { day ->
            days[day]?.setPairs(schedule.pairsByDate(currentDate))
            currentDate = currentDate.plusDays(1)
        }

        scheduleName = schedule.info.scheduleName +
                ". " + date.withDayOfWeek(1).toString("dd.MM.yyyy") +
                "-" + date.withDayOfWeek(7).toString("dd.MM.yyyy")
        mode = TableMode.Weekly
    }

    fun linesPerDay(): List<Int> {
        return days.values.map { day -> day.lines() }
    }
}