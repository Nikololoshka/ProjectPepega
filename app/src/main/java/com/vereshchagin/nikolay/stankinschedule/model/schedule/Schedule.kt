package com.vereshchagin.nikolay.stankinschedule.model.schedule

import com.google.gson.*
import com.vereshchagin.nikolay.stankinschedule.model.schedule.pair.DayOfWeek
import com.vereshchagin.nikolay.stankinschedule.model.schedule.pair.Pair
import java.lang.reflect.Type

class Schedule {

    private val weeks = linkedMapOf<DayOfWeek, ScheduleDay>()

    init {
        for (dayOfWeek in DayOfWeek.values()) {
            weeks[dayOfWeek] = ScheduleDay()
        }
    }

    fun add(pair: Pair) {
        weeks[pair.date.dayOfWeek()]!!.add(pair)
    }

    fun remove(pair: Pair) {
        weeks[pair.date.dayOfWeek()]!!.remove(pair)
    }

    /**
     * Сериализатор расписания.
     */
    class Serializer : JsonSerializer<Schedule> {
        override fun serialize(
            src: Schedule?,
            typeOfSrc: Type?,
            context: JsonSerializationContext?
        ): JsonElement {
            if (src == null) return JsonArray()

            val array = JsonArray()
            for (week in src.weeks.values) {
                for (pair in week.pairs) {
                    array.add(context?.serialize(pair, Pair::class.java))
                }
            }

            return array
        }
    }

    /**
     * Десериализатор расписания.
     */
    class Deserializer : JsonDeserializer<Schedule> {
        override fun deserialize(
            json: JsonElement?,
            typeOfT: Type?,
            context: JsonDeserializationContext?
        ): Schedule {
            if (json == null) {
                throw JsonParseException("Schedule json is null")
            }

            val schedule = Schedule()
            for (element in json.asJsonArray) {
                schedule.add(context!!.deserialize(element, Pair::class.java))
            }

            return schedule
        }
    }
}