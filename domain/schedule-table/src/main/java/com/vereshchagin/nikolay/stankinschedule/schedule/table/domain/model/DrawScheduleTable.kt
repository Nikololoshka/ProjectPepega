package com.vereshchagin.nikolay.stankinschedule.schedule.table.domain.model

import android.graphics.Color
import android.graphics.Paint
import android.text.TextPaint
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.*

class DrawScheduleTable(
    val scheduleName: String,
    val lines: List<Int>,
    tableDays: Map<DayOfWeek, ScheduleTableDay>,
    val pageHeight: Float,
    val pageWidth: Float,
    val tableColor: Int = Color.BLACK,
    val scale: Float = pageWidth / 1920f,
    val pagePadding: Float = pageHeight * 0.05f
) {
    val textPaint: TextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = tableColor
    }
    val linePaint: TextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = tableColor
        strokeWidth = 1f
    }

    val titleSize = 28f * scale
    val headerSize = 32f * scale
    val textSize = 14f * scale
    val textPadding = 4 * scale

    private val days: MutableMap<DayOfWeek, List<DrawScheduleTableCell>> = mutableMapOf()

    val drawWidth = pageWidth - 2 * pagePadding
    val drawHeight = pageHeight - 2 * pagePadding

    val titleHeight = textPaint.withSize(titleSize) { it.lineHeight() }
    val titleBottomPadding = pagePadding / 8

    val columnWidth = (drawWidth - headerSize) / COLUMN_COUNT
    val rowHeight = (drawHeight - headerSize - titleHeight - titleBottomPadding) / ROW_COUNT

    init {
        for ((index, lineCount) in lines.withIndex()) {
            val subRowHeight = rowHeight / lineCount

            val day = DayOfWeek.values()[index]
            val tableDay = tableDays.getOrElse(day) { ScheduleTableDay() }

            days[day] = tableDay
                .cells { pairs ->
                    pairs.joinToString(separator = "\n", transform = ::pairForTable)
                }.map { cell ->
                    DrawScheduleTableCell(
                        cell = cell,
                        width = (columnWidth * cell.columnSpan - 2 * textPadding).toInt(),
                        height = (subRowHeight * cell.rowSpan - 2 * textPadding).toInt(),
                        fontSize = textSize,
                        textPaint = textPaint
                    )
                }
        }
    }

    operator fun get(day: DayOfWeek): List<DrawScheduleTableCell> {
        return days.getOrElse(day) { emptyList() }
    }

    companion object {
        const val ROW_COUNT = 6
        const val COLUMN_COUNT = 8

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

fun ScheduleTable.toDraw(
    pageHeight: Float,
    pageWidth: Float,
    tableColor: Int = Color.BLACK,
): DrawScheduleTable {
    return DrawScheduleTable(
        scheduleName = scheduleName,
        lines = linesPerDay(),
        tableDays = days,
        pageHeight = pageHeight,
        pageWidth = pageWidth,
        tableColor = tableColor
    )
}