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

    val start: LocalTime
    val end: LocalTime
    val duration: Int

    constructor(parcel: Parcel) {
        start = parcel.readSerializable() as LocalTime
        end = parcel.readSerializable() as LocalTime
        duration = parcel.readInt()
    }

    constructor(startTime: String, endTime: String) {
        if (!STARTS.contains(startTime) || !ENDS.contains(endTime)) {
            throw IllegalArgumentException("Not parse time: $startTime - $endTime")
        }

        val formatter = DateTimeFormat.forPattern(TIME_PATTERN)
        start = LocalTime.parse(startTime, formatter)
        end = LocalTime.parse(endTime, formatter)
        duration = ENDS.indexOf(endTime) - STARTS.indexOf(startTime) + 1
    }

    constructor(obj: JsonElement) : this(
        obj.asJsonObject[JSON_START].asString,
        obj.asJsonObject[JSON_END].asString
    )

    fun intersect(other: Time) : Boolean {
        return (start >= other.start && end <= other.end) ||
            (start <= other.start && end >= other.end) ||
            (start <= other.end && end >= other.end)
    }

    fun toJson() : JsonElement {
        return JsonObject().apply {
            addProperty(JSON_START, start.toString(TIME_PATTERN))
            addProperty(JSON_END, end.toString(TIME_PATTERN))
        }
    }

    fun number(): Int {
        return STARTS.indexOf(start.toString(TIME_PATTERN))
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

        const val TIME_PATTERN = "HH:mm"
        const val JSON_START = "start"
        const val JSON_END = "end"

        val STARTS = listOf("8:30", "10:20", "12:20", "14:10", "16:00", "18:00", "19:40", "21:20")

        val ENDS = listOf("10:10", "12:00", "14:00", "15:50", "17:40", "19:30", "21:10", "22:50")
    }
}