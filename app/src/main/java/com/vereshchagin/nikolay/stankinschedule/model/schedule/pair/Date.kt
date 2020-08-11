package com.vereshchagin.nikolay.stankinschedule.model.schedule.pair

import android.os.Parcelable
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import kotlinx.android.parcel.Parcelize
import java.util.*

/**
 * Дата пары.
 */
@Parcelize
class Date(
    private val dates: TreeSet<DateItem> = sortedSetOf(),
    private var dayOfWeek: DayOfWeek? = null
) : Parcelable {

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
        if (dayOfWeek == null) {
            dayOfWeek = item.dayOfWeek()
        } else if (dayOfWeek != item.dayOfWeek()) {
            throw IllegalArgumentException("Invalid day of week: $dayOfWeek and ${item.dayOfWeek()}")
        }

        if (intersect(item)) {
            throw IllegalArgumentException("Date is intersect: $item and $this")
        }

        dates.add(item)
    }

    /**
     * Удаляет дату из дат пары.
     */
    fun remove(item: DateItem) = dates.remove(item)

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

    override fun toString(): String {
        return "[" + dates.joinToString(", ") + "]"
    }
}