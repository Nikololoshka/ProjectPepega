package com.vereshchagin.nikolay.stankinschedule.model.schedule.pair

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import org.joda.time.LocalTime
import org.joda.time.format.DateTimeFormat

/**
 * Время пары
 */
class Time : Parcelable {
    /**
     * Начало пары.
     */
    val start: LocalTime

    /**
     * Конец пары.
     */
    val end: LocalTime

    /**
     * Продолжительность пары.
     */
    val duration: Int

    constructor(parcel: Parcel) {
        start = parcel.readSerializable() as LocalTime
        end = parcel.readSerializable() as LocalTime
        duration = parcel.readInt()
    }

    /**
     * Конструктор времени пары.
     * @param startTime текст с началом пары.
     * @param endTime текст с концом пары.
     */
    constructor(startTime: String, endTime: String) {
        if (!STARTS.contains(startTime) || !ENDS.contains(endTime)) {
            throw IllegalArgumentException("Not parse time: $startTime - $endTime")
        }

        val formatter = DateTimeFormat.forPattern(TIME_PATTERN)
        start = LocalTime.parse(startTime, formatter)
        end = LocalTime.parse(endTime, formatter)
        duration = ENDS.indexOf(endTime) - STARTS.indexOf(startTime) + 1
    }

    /**
     * Конструктор времени пары.
     * @param obj JSON с временем пары.
     */
    constructor(obj: JsonElement) : this(
        obj.asJsonObject[JSON_START].asString,
        obj.asJsonObject[JSON_END].asString
    )

    /**
     * Определяет, пересекаются времена пары.
     */
    fun isIntersect(other: Time): Boolean {
        return (start >= other.start && end <= other.end) ||
                (start <= other.start && end >= other.end) ||
                (start <= other.end && end >= other.end)
    }

    /**
     * Возвращает JSON времени пары.
     */
    fun toJson(): JsonElement {
        return JsonObject().apply {
            addProperty(JSON_START, start.toString(TIME_PATTERN))
            addProperty(JSON_END, end.toString(TIME_PATTERN))
        }
    }

    fun startString(): String = start.toString(TIME_PATTERN)

    fun endString(): String = end.toString(TIME_PATTERN)

    /**
     * Номер начала пары по времени.
     * Например, 8:30 - 1 пара, 14:00 - 4.
     */
    fun number(): Int {
        return STARTS.indexOf(start.toString(TIME_PATTERN))
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Time

        if (start != other.start) return false
        if (end != other.end) return false
        if (duration != other.duration) return false

        return true
    }

    override fun hashCode(): Int {
        var result = start.hashCode()
        result = 31 * result + end.hashCode()
        result = 31 * result + duration
        return result
    }

    override fun toString(): String {
        return "${start.toString(TIME_PATTERN)}-${end.toString(TIME_PATTERN)}"
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeSerializable(start)
        parcel.writeSerializable(end)
        parcel.writeInt(duration)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {
        @JvmField
        val CREATOR = object : Parcelable.Creator<Time> {
            override fun createFromParcel(parcel: Parcel): Time {
                return Time(parcel)
            }

            override fun newArray(size: Int): Array<Time?> {
                return arrayOfNulls(size)
            }
        }

        private const val TIME_PATTERN = "H:mm"
        private const val JSON_START = "start"
        private const val JSON_END = "end"

        private val STARTS =
            listOf("8:30", "10:20", "12:20", "14:10", "16:00", "18:00", "19:40", "21:20")

        private val ENDS =
            listOf("10:10", "12:00", "14:00", "15:50", "17:40", "19:30", "21:10", "22:50")
    }
}