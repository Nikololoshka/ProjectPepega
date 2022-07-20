package com.vereshchagin.nikolay.stankinschedule.model.schedule.pair

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.JsonObject
import com.vereshchagin.nikolay.stankinschedule.model.schedule.DateDayOfWeekException
import com.vereshchagin.nikolay.stankinschedule.model.schedule.DateFrequencyException
import com.vereshchagin.nikolay.stankinschedule.model.schedule.DateParseException
import com.vereshchagin.nikolay.stankinschedule.model.schedule.json.JsonPairItem
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

    constructor(parcel: Parcel) : this(parcel.readSerializable() as LocalDate)

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

    /**
     * Конструктор единственной даты пары.
     * @param obj JSON объект с датой занятия.
     */
    constructor(obj: JsonObject) : this(obj[JSON_DATE].asString) {
        val frequency = obj[JSON_FREQUENCY].asString
        if (Frequency.of(frequency) != Frequency.ONCE) {
            throw DateFrequencyException(
                "Invalid parse json: $obj",
                this.toString(), Frequency.of(frequency)
            )
        }
    }

    override fun dayOfWeek(): DayOfWeek = dayOfWeek

    override fun frequency(): Frequency = Frequency.ONCE

    override fun toJson(): JsonObject {
        return JsonObject().apply {
            addProperty(JSON_DATE, date.toString(JSON_DATE_PATTERN_V2))
            addProperty(JSON_FREQUENCY, Frequency.ONCE.tag)
        }
    }

    override fun toJsonItem(): JsonPairItem.JsonDateItem {
        return JsonPairItem.JsonDateItem(date.toString(JSON_DATE_PATTERN_V2), Frequency.ONCE.tag)
    }

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

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeSerializable(date)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {
        @JvmField
        val CREATOR = object : Parcelable.Creator<DateSingle> {
            override fun createFromParcel(parcel: Parcel): DateSingle {
                return DateSingle(parcel)
            }

            override fun newArray(size: Int): Array<DateSingle?> {
                return arrayOfNulls(size)
            }
        }
    }
}