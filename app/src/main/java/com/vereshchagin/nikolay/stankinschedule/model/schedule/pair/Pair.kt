package com.vereshchagin.nikolay.stankinschedule.model.schedule.pair

import android.os.Parcelable
import com.google.gson.*
import kotlinx.parcelize.Parcelize

/**
 * Пара в расписании.
 * @param title название пары.
 * @param lecturer преподаватель.
 * @param classroom аудитория.
 * @param type тип пары.
 * @param subgroup подгруппа пары.
 * @param time время пары.
 * @param date даты проведения пары.
 */
@Parcelize
open class Pair(
    var title: String,
    var lecturer: String,
    var classroom: String,
    var type: Type,
    var subgroup: Subgroup,
    var time: Time,
    var date: Date,

    ) : Parcelable, Comparable<Pair> {

    constructor(
        other: Pair,
    ) : this(
        other.title,
        other.lecturer,
        other.classroom,
        other.type,
        other.subgroup,
        other.time,
        other.date
    )

    /**
     * Определяет, пересекаются ли пары по времени, дате и подгруппе.
     * @param other другая пара.
     */
    fun isIntersect(other: Pair): Boolean {
        return time.isIntersect(other.time) && date.intersect(other.date) &&
                subgroup.isIntersect(other.subgroup)
    }

    /**
     * Возвращает true, если пара может быть у этой подгруппы, иначе false.
     */
    fun isCurrently(subgroup: Subgroup): Boolean {
        return this.subgroup == Subgroup.COMMON || subgroup == Subgroup.COMMON || this.subgroup == subgroup
    }

    override fun compareTo(other: Pair): Int {
        if (time.start == other.time.start) {
            return subgroup.compareTo(other.subgroup)
        }
        return time.start.compareTo(other.time.start)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Pair

        if (title != other.title) return false
        if (lecturer != other.lecturer) return false
        if (classroom != other.classroom) return false
        if (type != other.type) return false
        if (subgroup != other.subgroup) return false
        if (time != other.time) return false
        if (date != other.date) return false

        return true
    }

    override fun hashCode(): Int {
        var result = title.hashCode()
        result = 31 * result + lecturer.hashCode()
        result = 31 * result + classroom.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + subgroup.hashCode()
        result = 31 * result + time.hashCode()
        result = 31 * result + date.hashCode()
        return result
    }

    override fun toString(): String {
        return "$title. $lecturer. $classroom. $type. $subgroup. $time. $date"
    }

    /**
     * Сериализатор пары.
     */
    class Serializer : JsonSerializer<Pair>, JsonDeserializer<Pair> {
        override fun serialize(
            src: Pair?,
            typeOfSrc: java.lang.reflect.Type?,
            context: JsonSerializationContext?,
        ): JsonElement {
            return if (src == null) {
                JsonObject()
            } else {
                JsonObject().apply {
                    addProperty(JSON_TITLE, src.title)
                    addProperty(JSON_LECTURER, src.lecturer)
                    addProperty(JSON_CLASSROOM, src.classroom)
                    addProperty(JSON_TYPE, src.type.tag)
                    addProperty(JSON_SUBGROUP, src.subgroup.tag)
                    add(JSON_TIME, src.time.toJson())
                    add(JSON_DATE, src.date.toJson())
                }
            }
        }

        override fun deserialize(
            json: JsonElement?,
            typeOfT: java.lang.reflect.Type?,
            context: JsonDeserializationContext?,
        ): Pair {
            if (json == null) {
                throw JsonParseException("Json is null")
            }

            val obj = json.asJsonObject
            return Pair(
                obj[JSON_TITLE].asString,
                obj[JSON_LECTURER].asString,
                obj[JSON_CLASSROOM].asString,
                Type.of(obj[JSON_TYPE].asString),
                Subgroup.of(obj[JSON_SUBGROUP].asString),
                Time(obj[JSON_TIME]),
                Date(obj[JSON_DATE])
            )
        }
    }

    companion object {
        const val JSON_TITLE = "title"
        const val JSON_LECTURER = "lecturer"
        const val JSON_CLASSROOM = "classroom"
        const val JSON_TYPE = "type"
        const val JSON_SUBGROUP = "subgroup"
        const val JSON_TIME = "time"
        const val JSON_DATE = "dates"
    }
}