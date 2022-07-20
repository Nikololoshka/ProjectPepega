package com.vereshchagin.nikolay.stankinschedule.model.schedule.pair

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.JsonObject
import com.vereshchagin.nikolay.stankinschedule.model.schedule.DateDayOfWeekException
import com.vereshchagin.nikolay.stankinschedule.model.schedule.DateFrequencyException
import com.vereshchagin.nikolay.stankinschedule.model.schedule.DateParseException
import com.vereshchagin.nikolay.stankinschedule.model.schedule.json.JsonPairItem
import org.joda.time.Days
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat

/**
 * Диапазон дат в расписании с определенной периодичностью.
 */
class DateRange : DateItem {

    /**
     * Начало диапазона.
     */
    val start: LocalDate

    /**
     * Конец диапазона.
     */
    val end: LocalDate

    /**
     * Периодичность.
     */
    private val frequency: Frequency

    /**
     * День недели.
     */
    private lateinit var dayOfWeek: DayOfWeek

    constructor(parcel: Parcel) {
        start = parcel.readSerializable() as LocalDate
        end = parcel.readSerializable() as LocalDate
        frequency = parcel.readSerializable() as Frequency

        init()
    }

    /**
     * Конструктор диапазона дат.
     * @param firstText текст первой даты диапазона.
     * @param secondText текст второй даты диапазона.
     * @param frequencyDate периодичность даты.
     * @param pattern шаблон распознавания.
     */
    constructor(
        firstText: String,
        secondText: String,
        frequencyDate: Frequency,
        pattern: String = JSON_DATE_PATTERN_V2,
    ) {
        try {
            val (parseStart, parseEnd) = parseDates(firstText, secondText, pattern)
            start = parseStart
            end = parseEnd
            frequency = frequencyDate

        } catch (e: Exception) {
            throw DateParseException(
                "Invalid parse date: $firstText and $secondText",
                "$firstText - $secondText",
                e
            )
        }

        init()
    }

    /**
     * Конструктор диапазона дат.
     * @param text текст с диапазоном.
     * @param frequencyDate периодичность даты.
     * @param pattern шаблон распознавания.
     */
    constructor(text: String, frequencyDate: Frequency, pattern: String = JSON_DATE_PATTERN_V2) {
        var dates = text.split('/')
        if (dates.size != 2) {
            // старый формат
            dates = text.split('-')
            if (dates.size != 2) {
                throw DateParseException(
                    "Invalid date text: $text, $dates, frequency: $frequencyDate",
                    text
                )
            }
        }

        val (firstText, secondText) = dates

        try {
            val (parseStart, parseEnd) = parseDates(firstText, secondText, pattern)
            start = parseStart
            end = parseEnd
            frequency = frequencyDate

        } catch (e: Exception) {
            throw DateParseException(
                "Invalid parse date: $firstText and $secondText",
                "$firstText - $secondText",
                e
            )
        }

        init()
    }

    /**
     * Конструктор диапазона дат.
     * @param firstDate дата начала диапазона.
     * @param secondDate дата конца диапазона.
     * @param frequencyDate периодичность диапазона дат.
     */
    constructor(firstDate: LocalDate, secondDate: LocalDate, frequencyDate: Frequency) {
        start = firstDate
        end = secondDate
        frequency = frequencyDate

        init()
    }

    /**
     * Конструктор диапазона дат.
     * @param obj JSON с диапазоном дат.
     */
    constructor(obj: JsonObject) : this(
        obj[JSON_DATE].asString,
        Frequency.of(obj[JSON_FREQUENCY].asString)
    )

    /**
     * Парсит дату начала и конца из строк по шаблону.
     * Возвращает распознанные даты.
     */
    private fun parseDates(
        firstText: String,
        secondText: String,
        pattern: String,
    ): Pair<LocalDate, LocalDate> {

        var parseStart: LocalDate
        var parseEnd: LocalDate

        try {
            val formatter = DateTimeFormat.forPattern(pattern)
            parseStart = formatter.parseLocalDate(firstText)
            parseEnd = formatter.parseLocalDate(secondText)

        } catch (e: Exception) {
            // старый формат
            val formatter = DateTimeFormat.forPattern(JSON_DATE_PATTERN)
            parseStart = formatter.parseLocalDate(firstText)
            parseEnd = formatter.parseLocalDate(secondText)
        }

        return parseStart to parseEnd
    }

    /**
     * Инициализирует до конца объект.
     */
    private fun init() {
        if (DayOfWeek.of(start) != DayOfWeek.of(end)) {
            throw DateDayOfWeekException(
                "Invalid day of week: $start - $end"
            )
        }
        dayOfWeek = DayOfWeek.of(start)

        val days = Days.daysBetween(start, end).days
        if (days <= 0 || days % frequency.period != 0) {
            throw DateFrequencyException(
                "Invalid frequency: $start - $end, ${frequency.tag}",
                this.toString(), frequency
            )
        }
    }

    override fun dayOfWeek(): DayOfWeek = dayOfWeek

    override fun frequency(): Frequency = frequency

    override fun toJson(): JsonObject {
        return JsonObject().apply {
            addProperty(
                JSON_DATE,
                start.toString(JSON_DATE_PATTERN_V2) + "/" + end.toString(JSON_DATE_PATTERN_V2)
            )
            addProperty(JSON_FREQUENCY, frequency.tag)
        }
    }

    override fun toJsonItem(): JsonPairItem.JsonDateItem {
        return JsonPairItem.JsonDateItem(
            start.toString(JSON_DATE_PATTERN_V2) + "/" + end.toString(JSON_DATE_PATTERN_V2),
            frequency.tag
        )
    }

    override fun intersect(item: DateItem): Boolean {
        if (item is DateSingle) {
            var it = start
            while (it.isBefore(end) || it == end) {
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

            while (firstIt.isBefore(end) || firstIt == end) {
                while (secondIt.isBefore(item.end) || secondIt == item.end) {
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

    override fun clone(): DateItem {
        return DateRange(start, end, frequency)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DateRange

        if (start != other.start) return false
        if (end != other.end) return false
        if (frequency != other.frequency) return false
        if (dayOfWeek != other.dayOfWeek) return false

        return true
    }

    override fun hashCode(): Int {
        var result = start.hashCode()
        result = 31 * result + end.hashCode()
        result = 31 * result + frequency.hashCode()
        result = 31 * result + dayOfWeek.hashCode()
        return result
    }

    override fun toString(): String {
        return "$start-$end"
    }

    fun toString(format: String, delimiter: String): String {
        return start.toString(format) + delimiter + end.toString(format)
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
        val CREATOR = object : Parcelable.Creator<DateRange> {
            override fun createFromParcel(parcel: Parcel): DateRange {
                return DateRange(parcel)
            }

            override fun newArray(size: Int): Array<DateRange?> {
                return arrayOfNulls(size)
            }
        }
    }
}