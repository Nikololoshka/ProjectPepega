package com.vereshchagin.nikolay.stankinschedule.schedule.domain.model

import com.vereshchagin.nikolay.stankinschedule.model.schedule.pair.DayOfWeek
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat

/**
 * Единственная дата пары.
 */
class DateSingle : DateItem {

    /**
     * Дата.
     */
    val date: LocalDate

    /**
     * День недели.
     */
    private val dayOfWeek: DayOfWeek

    /**
     * Конструктор единственной даты пары.
     * @param text текст с датой.
     * @param pattern шаблон распознавания.
     */
    constructor(text: String, pattern: String = JSON_DATE_PATTERN_V2) {
        try {
            val parseDate: LocalDate = try {
                DateTimeFormat.forPattern(pattern).parseLocalDate(text)
            } catch (e: Exception) {
                // старый метод
                DateTimeFormat.forPattern(JSON_DATE_PATTERN).parseLocalDate(text)
            }

            date = parseDate
            dayOfWeek = DayOfWeek.of(date)

        } catch (e: DateDayOfWeekException) {
            throw e

        } catch (e: Exception) {
            throw DateParseException("Invalid parse date: $text", text, e)
        }
    }

    /**
     * Конструктор единственной даты пары.
     * @param date дата занятия.
     */
    constructor(date: LocalDate) {
        this.date = date
        dayOfWeek = DayOfWeek.of(this.date)
    }


    override fun dayOfWeek(): DayOfWeek = dayOfWeek

    override fun frequency(): Frequency = Frequency.ONCE

    override fun intersect(item: DateItem): Boolean {
        if (item is DateSingle) {
            return this.date == item.date
        }

        if (item is DateRange) {
            return item.intersect(this)
        }

        throw IllegalArgumentException("Invalid intersect object: $item")
    }

    override fun isBefore(item: DateItem): Boolean {
        if (item is DateSingle) {
            return date.isBefore(item.date)
        }

        if (item is DateRange) {
            return date.isBefore(item.start) && date.isBefore(item.end)
        }

        throw IllegalArgumentException("Invalid compare object: $item")
    }

    override fun clone(): DateItem {
        return DateSingle(date)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DateSingle

        if (date != other.date) return false
        if (dayOfWeek != other.dayOfWeek) return false

        return true
    }

    override fun hashCode(): Int {
        var result = date.hashCode()
        result = 31 * result + dayOfWeek.hashCode()
        return result
    }

    override fun toString(): String {
        return date.toString()
    }

    fun toString(format: String): String {
        return date.toString(format)
    }
}