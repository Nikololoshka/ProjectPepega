package com.vereshchagin.nikolay.stankinschedule.model.schedule.pair

import android.os.Parcelable
import com.google.gson.*
import kotlinx.android.parcel.Parcelize

/**
 * Пара в расписании.
 */
@Parcelize
class Pair(
    var title: String,
    var lecturer: String,
    var classroom: String,
    var type: Type,
    var subgroup: Subgroup,
    var time: Time,
    var date: Date
) : Parcelable, Comparable<Pair> {

    fun separate(other: Pair) : Boolean {
        return subgroup.separate(other.subgroup)
    }

    fun intersect(other: Pair) : Boolean {
        return time.intersect(other.time) && date.intersect(other.date)
    }

    override fun toString(): String {
        return "$title. $lecturer. $classroom. $type. $subgroup. $time. $date"
    }

    /**
     * Сериализатор пары.
     */
    class Serializer : JsonSerializer<Pair> {
        override fun serialize(
            src: Pair?,
            typeOfSrc: java.lang.reflect.Type?,
            context: JsonSerializationContext?
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
    }

    /**
     * Десериализатор пары.
     */
    class Deserializer : JsonDeserializer<Pair> {
        override fun deserialize(
            json: JsonElement?,
            typeOfT: java.lang.reflect.Type?,
            context: JsonDeserializationContext?
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

    override fun compareTo(other: Pair): Int {
        if (time.start == other.time.start) {
            return subgroup.compareTo(other.subgroup)
        }
        return time.start.compareTo(other.time.start)
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