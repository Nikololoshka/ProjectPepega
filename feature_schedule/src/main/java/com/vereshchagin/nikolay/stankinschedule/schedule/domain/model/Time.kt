package com.vereshchagin.nikolay.stankinschedule.schedule.domain.model

import org.joda.time.LocalTime
import org.joda.time.format.DateTimeFormat

/**
 * Время пары
 */
class Time(startTime: String, endTime: String) {
    /**
     * Начало пары.
     */
    val start: LocalTime

    /**
     * Конец пары.
     */
    val end: LocalTime

    /**
     * Продолжительность пары.
     */
    val duration: Int

    init {
        val formatter = DateTimeFormat.forPattern(TIME_PATTERN)
        start = LocalTime.parse(startTime, formatter)
        end = LocalTime.parse(endTime, formatter)
        if (!STARTS.contains(start.toString(TIME_PATTERN))
            || !ENDS.contains(end.toString(TIME_PATTERN))
        ) {
            throw IllegalArgumentException("Not parse time: $startTime - $endTime")
        }
        duration = ENDS.indexOf(endTime) - STARTS.indexOf(startTime) + 1
    }

    /**
     * Определяет, пересекаются времена пары.
     */
    fun isIntersect(other: Time): Boolean {
        return (start >= other.start && end <= other.end) ||
                (start <= other.start && end >= other.end) ||
                (start <= other.end && end >= other.end)
    }

    fun startString(): String = start.toString(TIME_PATTERN)

    fun endString(): String = end.toString(TIME_PATTERN)

    /**
     * Номер начала пары по времени.
     * Например, 8:30 - 1 пара, 14:00 - 4.
     */
    fun number(): Int {
        return STARTS.indexOf(start.toString(TIME_PATTERN))
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Time

        if (start != other.start) return false
        if (end != other.end) return false
        if (duration != other.duration) return false

        return true
    }

    override fun hashCode(): Int {
        var result = start.hashCode()
        result = 31 * result + end.hashCode()
        result = 31 * result + duration
        return result
    }

    override fun toString(): String {
        return "${start.toString(TIME_PATTERN)}-${end.toString(TIME_PATTERN)}"
    }

    companion object {
        private const val TIME_PATTERN = "H:mm"

        fun fromString(time: String): Time {
            val (start, end) = time.split('-')
            return Time(start, end)
        }

        val STARTS =
            listOf("8:30", "10:20", "12:20", "14:10", "16:00", "18:00", "19:40", "21:20")

        val ENDS =
            listOf("10:10", "12:00", "14:00", "15:50", "17:40", "19:30", "21:10", "22:50")
    }
}