package com.vereshchagin.nikolay.stankinschedule.schedule.domain.model

import com.vereshchagin.nikolay.stankinschedule.model.schedule.pair.DayOfWeek

/**
 * Абстрактный класс дат пары в расписании.
 */
sealed class DateItem : Comparable<DateItem> {

    companion object {
        const val JSON_DATE_SEP = "/"
        const val JSON_DATE = "date"
        const val JSON_FREQUENCY = "frequency"
        const val JSON_DATE_PATTERN = "yyyy.MM.dd"
        const val JSON_DATE_PATTERN_V2 = "yyyy-MM-dd"
    }

    /**
     * Возвращает день недели даты.
     */
    abstract fun dayOfWeek(): DayOfWeek

    /**
     * Возвращает периодичность даты.
     */
    abstract fun frequency(): Frequency

    /**
     * Определяет, пересекаются ли даты между собой.
     */
    abstract fun intersect(item: DateItem): Boolean

    /**
     * Определяет, находиться ли дата раньше другой.
     */
    abstract fun isBefore(item: DateItem): Boolean

    /**
     * Возвращает копию объекта.
     */
    abstract fun clone(): DateItem

    override fun compareTo(other: DateItem): Int {
        if (this == other) {
            return 0
        }
        return if (isBefore(other)) -1 else 1
    }

    abstract override fun equals(other: Any?): Boolean

    abstract override fun hashCode(): Int

    abstract override fun toString(): String
}