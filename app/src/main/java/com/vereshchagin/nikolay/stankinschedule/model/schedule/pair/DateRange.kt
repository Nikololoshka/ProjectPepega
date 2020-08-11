package com.vereshchagin.nikolay.stankinschedule.model.schedule.pair

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.JsonObject
import com.vereshchagin.nikolay.stankinschedule.utils.DateUtils
import org.joda.time.DateTime
import org.joda.time.Days
import org.joda.time.format.DateTimeFormat

class DateRange : DateItem {

    internal val start: DateTime
    internal val end: DateTime
    private val frequency: Frequency
    private lateinit var dayOfWeek: DayOfWeek

    constructor(parcel: Parcel) {
        start = parcel.readSerializable() as DateTime
        end = parcel.readSerializable() as DateTime
        frequency = parcel.readSerializable() as Frequency

        init()
    }

    constructor(text: String, frequencyDate: Frequency) {
        val dates = text.split('-')
        if (dates.size != 2) {
            throw IllegalArgumentException("Invalid date text: $text, $dates, frequency: $frequencyDate")
        }

        val firstText = dates[0]
        val secondText = dates[1]

        try {
            val formatter = DateTimeFormat.forPattern(JSON_DATE_PATTERN)
            start = formatter.parseDateTime(firstText)
            end = formatter.parseDateTime(secondText)
            frequency = frequencyDate

        } catch (e: Exception) {
            throw IllegalArgumentException("Invalid parse date: $firstText and $secondText", e)
        }

        init()
    }

    constructor(firstDate: DateTime, secondDate: DateTime, frequencyDate: Frequency) {
        start = firstDate
        end = secondDate
        frequency = frequencyDate

        init()
    }

    constructor(obj: JsonObject) : this (
        obj[JSON_DATE].asString,
        Frequency.of(obj[JSON_FREQUENCY].asString)
    )

    private fun init() {
        if (DayOfWeek.of(start) != DayOfWeek.of(end)) {
            throw IllegalArgumentException("Invalid day of week: $start - $end")
        }
        dayOfWeek = DayOfWeek.of(start)

        val days = Days.daysBetween(start, end).days
        if (days == 0 || days % frequency.period != 0) {
            throw IllegalArgumentException("Invalid frequency: $start - $end, ${frequency.tag}")
        }
    }

    override fun dayOfWeek(): DayOfWeek = dayOfWeek

    override fun frequency(): Frequency = frequency

    override fun toJson(): JsonObject {
        return JsonObject().apply {
            addProperty(
                JSON_DATE,
                start.toString(JSON_DATE_PATTERN) + "-" + end.toString(JSON_DATE_PATTERN)
            )
            addProperty(JSON_FREQUENCY, frequency.tag)
        }
    }

    override fun intersect(item: DateItem): Boolean {
        if (item is DateSingle) {
            var it = start
            while (it.isBefore(end)) {
                if (it == item.date) {
                    return true
                }
                it = it.plusDays(frequency.period)
            }
            return false
        }

        if (item is DateRange) {
            var firstIt = start
            var secondIt = item.start

            while (firstIt.isBefore(end)) {
                while (secondIt.isBefore(item.end)) {
                    if (firstIt == secondIt) {
                        return true
                    }
                    secondIt = secondIt.plusDays(item.frequency.period)
                }
                firstIt = firstIt.plusDays(frequency.period)
            }

            return false
        }

        throw IllegalArgumentException("Invalid intersect object: $item")
    }

    override fun isBefore(item: DateItem): Boolean {
        if (item is DateSingle) {
            return start.isBefore(item.date) && end.isBefore(item.date)
        }

        if (item is DateRange) {
            return start.isBefore(item.start) && end.isBefore(item.end)
        }

        throw IllegalArgumentException("Invalid compare object: $item")
    }

    override fun toString(): String {
        return start.toString(DateUtils.PRETTY_FORMAT) +
            "-" +  end.toString(DateUtils.PRETTY_FORMAT) + " " + frequency.tag
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeSerializable(start)
        parcel.writeSerializable(end)
        parcel.writeSerializable(frequency)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {
        @JvmField
        val CREATOR = object: Parcelable.Creator<DateRange> {
            override fun createFromParcel(parcel: Parcel): DateRange {
                return DateRange(parcel)
            }

            override fun newArray(size: Int): Array<DateRange?> {
                return arrayOfNulls(size)
            }
        }
    }
}