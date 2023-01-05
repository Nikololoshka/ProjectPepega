package com.vereshchagin.nikolay.stankinschedule.schedule.table.domain.model

import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.*
import org.joda.time.LocalDate

class ScheduleTable {

    private val days: Map<DayOfWeek, ScheduleTableDay> = buildMap(
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

    fun prepareForDraw(): DrawScheduleTable {
        return DrawScheduleTable(
            scheduleName = scheduleName,
            days = days.mapValues { day ->
                day.value.cells { pairs ->
                    pairs.joinToString(separator = "\n", transform = ::pairForTable)
                }
            },
            lines = linesPerDay()
        )
    }

    fun linesPerDay(): List<Int> {
        return days.values.map { day -> day.lines() }
    }

    companion object {

        fun pairForTable(pair: PairModel): String {
            // Title. Lecture. Type. Subgroup. Classroom. [Date1, Date2...]
            return buildString {
                append(pair.title)
                append(". ")
                append(if (pair.lecturer.isEmpty()) "" else pair.lecturer + ". ")
                append(
                    when (pair.type) {
                        Type.LECTURE -> "Лекция"
                        Type.SEMINAR -> "Семинар"
                        Type.LABORATORY -> "Лабораторные занятия"
                    }
                )
                append(". ")
                append(
                    if (pair.subgroup.isShow()) {
                        when (pair.subgroup) {
                            Subgroup.A -> "(А)"
                            Subgroup.B -> "(Б)"
                            Subgroup.COMMON -> ""
                        } + ". "
                    } else {
                        ""
                    }
                )
                append(if (pair.classroom.isEmpty()) "" else pair.classroom + ". ")
                append("[")
                append(pair.date.joinToString(", ") { date ->
                    when (date) {
                        is DateSingle -> {
                            date.toString("dd.MM")
                        }
                        is DateRange -> {
                            date.toString(
                                "dd.MM", "-"
                            ) + " " + when (date.frequency()) {
                                Frequency.ONCE -> ""
                                Frequency.EVERY -> "к.н."
                                Frequency.THROUGHOUT -> "ч.н."
                            }
                        }
                    }
                })
                append("]")
            }
        }

    }
}