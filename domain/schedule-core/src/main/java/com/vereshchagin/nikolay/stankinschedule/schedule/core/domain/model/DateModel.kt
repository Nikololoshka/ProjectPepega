package com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model

import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.exceptions.DateDayOfWeekException
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.exceptions.DateIntersectException
import org.joda.time.LocalDate
import java.util.*

/**
 * Дата пары.
 */
class DateModel : Cloneable, Iterable<DateItem> {

    private val dates: TreeSet<DateItem> = sortedSetOf()
    private var dayOfWeek: DayOfWeek? = null

    /**
     * Добавляет дату к датам пары.
     * @param item добавляемая пара.
     */
    fun add(item: DateItem) {
        possibleChange(null, item)

        dates.add(item)
        dayOfWeek = item.dayOfWeek()
    }

    /**
     * Удаляет дату из дат пары.
     * @param item удаляемая пара.
     */
    fun remove(item: DateItem?) {
        if (item != null) {
            dates.remove(item)
            if (dates.isEmpty()) {
                dayOfWeek = null
            }
        }
    }

    /**
     * Удаляет дату из дат пары по позиции по порядку.
     * @param position позиция, с которой необходимо удалить дату.
     */
    fun remove(position: Int): DateItem {
        val item = dates.elementAt(position)
        dates.removeIf { item == it }
        if (dates.isEmpty()) {
            dayOfWeek = null
        }
        return item
    }

    /**
     * Возвращает
     */
    fun get(position: Int): DateItem = dates.elementAt(position)

    /**
     * Дата начала из дат в паре.
     */
    fun startDate(): LocalDate? {
        if (dates.isEmpty()) {
            return null
        }

        return when (val startDate = dates.first()) {
            is DateSingle -> startDate.date
            is DateRange -> startDate.start
        }
    }

    /**
     * Дата конца из дат в паре.
     */
    fun endDate(): LocalDate? {
        if (dates.isEmpty()) {
            return null
        }

        return when (val startDate = dates.last()) {
            is DateSingle -> startDate.date
            is DateRange -> startDate.end
        }
    }

    /**
     * Определяет, пересекается ли даты пары с сравниваемой датой.
     */
    fun intersect(item: DateItem): Boolean {
        for (date in dates) {
            if (date.intersect(item)) {
                return true
            }
        }
        return false
    }

    /**
     * Определяет, пересекается ли даты пары с сравниваемой датой.
     */
    fun intersect(other: DateModel): Boolean {
        for (date in other.dates) {
            if (intersect(date)) {
                return true
            }
        }
        return false
    }

    /**
     * Определяет, пересекается ли даты пары с сравниваемой датой.
     */
    fun intersect(item: LocalDate): Boolean {
        val dateItem = DateSingle(item)
        return intersect(dateItem)
    }

    /**
     * Возвращает день недели.
     */
    fun dayOfWeek() = dayOfWeek!!

    /**
     * Возвращает Json даты.
     */
    /*
    fun toJsonItems(): List<JsonPairItem.JsonDateItem> {
        return dates.map { date -> date.toJsonItem() }
    }

     */

    override fun iterator(): Iterator<DateItem> = dates.iterator()

    /**
     * Проверяет, является ли дата пустой.
     */
    fun isEmpty(): Boolean = dates.isEmpty()

    /**
     * Преобразует дату в список из дат пары.
     */
    fun toList(): MutableList<DateItem> = dates.toMutableList()

    public override fun clone(): DateModel {
        val date = DateModel()
        for (d in dates) {
            date.add(d.clone())
        }
        return date
    }

    /**
     * Проверяет, можно ли заменить дату в датах.
     * @param oldDate старая дата.
     * @param newDate новая дата.
     * @throws DateDayOfWeekException если нет совпадения по дню недели.
     * @throws DateIntersectException если даты пересекаются.
     */
    @Throws(DateDayOfWeekException::class, DateIntersectException::class)
    fun possibleChange(oldDate: DateItem?, newDate: DateItem) {
        if (!(oldDate != null && dates.size == 1)) {
            if (dayOfWeek != null && dayOfWeek != newDate.dayOfWeek()) {
                throw DateDayOfWeekException(
                    "Invalid day of week: ${newDate.dayOfWeek()} and $dayOfWeek"
                )
            }
        }

        for (date in dates) {
            if (date != oldDate) {
                if (date.intersect(newDate)) {
                    throw DateIntersectException(
                        "Date is intersect: $date and $newDate",
                        date,
                        newDate
                    )
                }
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DateModel

        if (dates != other.dates) return false
        if (dayOfWeek != other.dayOfWeek) return false

        return true
    }

    override fun hashCode(): Int {
        var result = dates.hashCode()
        result = 31 * result + (dayOfWeek?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "[" + dates.joinToString(", ") + "]"
    }
}