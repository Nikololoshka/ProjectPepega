package com.vereshchagin.nikolay.stankinschedule.model.schedule.pair

import android.content.Context
import android.os.Parcelable
import com.google.gson.JsonObject
import org.joda.time.LocalDate

/**
 * Абстактный класс дат пары в расписании.
 */
abstract class DateItem : Comparable<DateItem>, Parcelable {

    companion object {
        const val JSON_DATE = "date"
        const val JSON_FREQUENCY = "frequency"
        const val JSON_DATE_PATTERN = "yyyy.MM.dd"
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
     * Возвращает JsonObject даты.
     */
    abstract fun toJson() : JsonObject

    /**
     * Определяет, пересекаются ли даты между собой.
     */
    abstract fun intersect(item: DateItem) : Boolean

    /**
     * Определяет, находиться ли дата раньше другой.
     */
    abstract fun isBefore(item: DateItem) : Boolean

    /**
     * Возвращает начало дата.
     */
    abstract fun startDate() : LocalDate

    /**
     * Возвращает конец даты.
     */
    abstract fun endDate() : LocalDate

    override fun compareTo(other: DateItem): Int {
        return if (isBefore(other)) -1 else 1
    }

    abstract override fun equals(other: Any?): Boolean

    abstract override fun hashCode(): Int

    abstract override fun toString(): String

    abstract fun toString(context: Context) : String
}