package com.vereshchagin.nikolay.stankinschedule.model.schedule

import com.google.gson.*
import com.vereshchagin.nikolay.stankinschedule.model.schedule.pair.DayOfWeek
import com.vereshchagin.nikolay.stankinschedule.model.schedule.pair.Pair
import org.joda.time.DateTime
import org.joda.time.DateTimeConstants
import org.joda.time.LocalDate
import java.lang.reflect.Type
import java.util.*

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

    fun startDate(): LocalDate? {
        var start: LocalDate? = null

        for (day in weeks.values) {
            val firstDay = day.startDate()
            if (firstDay != null) {
                if (start == null) {
                    start = firstDay
                } else {
                    if (firstDay < start) {
                        start = firstDay
                    }
                }
            }
        }
        return start
    }

    fun endDate(): LocalDate? {
        var last: LocalDate? = null

        for (day in weeks.values) {
            val lastDay = day.endDate()
            if (lastDay != null) {
                if (last == null) {
                    last = lastDay
                } else {
                    if (lastDay > last) {
                        last = lastDay
                    }
                }
            }
        }
        return last
    }

    fun pairsByDate(date: Calendar): ArrayList<Pair> {
        return pairsByDate(LocalDate(date))
    }

    fun pairsByDate(date: DateTime): ArrayList<Pair> {
        return pairsByDate(date.toLocalDate())
    }

    fun pairsByDate(date: LocalDate): ArrayList<Pair> {
        if (date.dayOfWeek == DateTimeConstants.SUNDAY) {
            return arrayListOf()
        }

        val dayOfWeek = DayOfWeek.of(date)
        return weeks[dayOfWeek]!!.pairsByDate(date)
    }

    fun possibleChangePair(old: Pair?, new: Pair) {
        weeks[new.date.dayOfWeek()]!!.possibleChangePair(old, new)
    }

    fun changePair(old: Pair?, new: Pair) {
        possibleChangePair(old, new)

        if (old != null) { remove(old) }
        add(new)
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