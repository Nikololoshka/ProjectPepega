package com.vereshchagin.nikolay.stankinschedule.model.schedule.pair

import android.content.Context
import android.os.Parcelable
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.vereshchagin.nikolay.stankinschedule.utils.extensions.removeIfJava7
import kotlinx.parcelize.Parcelize
import org.joda.time.LocalDate
import java.util.*

/**
 * Дата пары.
 * @param dates контейнер с датами пары.
 * @param dayOfWeek день недели дат пары.
 */
@Parcelize
class Date(
    private val dates: TreeSet<DateItem> = sortedSetOf(),
    private var dayOfWeek: DayOfWeek? = null
) : Parcelable, Cloneable, Iterable<DateItem> {

    /**
     * Конструктор объекта дат пары.
     * @param obj JSON объект с датами пары.
     */
    constructor(obj: JsonElement) : this() {
        val dateArray = obj.asJsonArray
        for (jsonDate in dateArray) {
            val json = jsonDate.asJsonObject
            val frequency = Frequency.of(json[DateItem.JSON_FREQUENCY].asString)

            if (frequency == Frequency.ONCE) {
                add(DateSingle(json))
            } else {
                add(DateRange(json))
            }
        }
    }

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
        dates.removeIfJava7 { item == it }
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
        return dates.first().startDate()
    }

    /**
     * Дата конца из дат в паре.
     */
    fun endDate(): LocalDate? {
        if (dates.isEmpty()) {
            return null
        }
        return dates.last().endDate()
    }

    /**
     * Определяет, пересекается ли даты пары с сравниваемой датой.
     */
    fun intersect(item: DateItem) : Boolean {
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
    fun intersect(other: Date) : Boolean {
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
    fun intersect(item: LocalDate) : Boolean {
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
    fun toJson() : JsonElement {
        val json = JsonArray()
        for (date in dates) {
            json.add(date.toJson())
        }
        return json
    }

    override fun iterator(): Iterator<DateItem> = dates.iterator()

    /**
     * Проверяет, является ли дата пустой.
     */
    fun isEmpty(): Boolean = dates.isEmpty()

    /**
     * Преобразует дату в список из дат пары.
     */
    fun toList(): MutableList<DateItem> = dates.toMutableList()

    public override fun clone() : Date {
        val date = Date()
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

        other as Date

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