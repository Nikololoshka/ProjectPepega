package com.vereshchagin.nikolay.stankinschedule.model.schedule.pair

import android.os.Parcelable
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.vereshchagin.nikolay.stankinschedule.utils.removeIfJava7
import kotlinx.android.parcel.Parcelize
import org.joda.time.LocalDate
import java.util.*

/**
 * Дата пары.
 */
@Parcelize
class Date(
    private val dates: TreeSet<DateItem> = sortedSetOf(),
    private var dayOfWeek: DayOfWeek? = null
) : Parcelable, Iterable<DateItem> {

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
     */
    fun add(item: DateItem) {
        possibleChange(null, item)

        dates.add(item)
        dayOfWeek = item.dayOfWeek()
    }

    /**
     * Удаляет дату из дат пары.
     */
    fun remove(item: DateItem?) {
        dates.remove(item)
        if (dates.isEmpty()) {
            dayOfWeek = null
        }
    }

    fun remove(position: Int): DateItem {
        val item = dates.elementAt(position)
        dates.removeIfJava7 { item == it }
        if (dates.isEmpty()) {
            dayOfWeek = null
        }
        return item
    }

    fun get(position: Int): DateItem = dates.elementAt(position)

    fun startDate(): LocalDate? {
        if (dates.isEmpty()) {
            return null
        }
        return dates.first().startDate()
    }

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

    fun isEmpty(): Boolean = dates.isEmpty()

    fun toList(): MutableList<DateItem> = dates.toMutableList()

    fun clone() : Date {
        val date = Date()
        for (d in this) {
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