package com.vereshchagin.nikolay.stankinschedule.model.schedule.pair

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.JsonObject
import com.vereshchagin.nikolay.stankinschedule.utils.DateUtils
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

/**
 * Единождая дата пары.
 */
class DateSingle : DateItem {

    internal val date: DateTime
    private val dayOfWeek: DayOfWeek

    constructor(parcel: Parcel) : this(parcel.readSerializable() as DateTime)

    constructor(text: String) {
        try {
            date = DateTimeFormat.forPattern(JSON_DATE_PATTERN).parseDateTime(text)
            dayOfWeek = DayOfWeek.of(date)
        } catch (e: Exception) {
            throw IllegalArgumentException("Invalid parse date: $text", e)
        }
    }

    constructor(datetime: DateTime) {
        date = datetime
        dayOfWeek = DayOfWeek.of(date)
    }

    constructor(obj: JsonObject) : this(obj[JSON_DATE].asString) {
        val frequency = obj[JSON_FREQUENCY].asString
        if (Frequency.of(frequency) != Frequency.ONCE) {
            throw IllegalArgumentException("Invalid parse json: $obj")
        }
    }

    override fun dayOfWeek(): DayOfWeek = dayOfWeek

    override fun frequency(): Frequency = Frequency.ONCE

    override fun toJson(): JsonObject {
        return JsonObject().apply {
            addProperty(JSON_DATE, date.toString(JSON_DATE_PATTERN))
            addProperty(JSON_FREQUENCY, Frequency.ONCE.tag)
        }
    }

    override fun intersect(item: DateItem): Boolean {
        if (item is DateSingle) {
           return date == item.date
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

    override fun toString(): String {
        return date.toString(DateUtils.PRETTY_FORMAT)
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