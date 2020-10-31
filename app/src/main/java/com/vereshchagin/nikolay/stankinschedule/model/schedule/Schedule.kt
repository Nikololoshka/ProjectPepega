package com.vereshchagin.nikolay.stankinschedule.model.schedule

import com.google.gson.*
import com.vereshchagin.nikolay.stankinschedule.model.schedule.pair.DayOfWeek
import com.vereshchagin.nikolay.stankinschedule.model.schedule.pair.Pair
import org.joda.time.DateTimeConstants
import org.joda.time.LocalDate
import java.lang.reflect.Type

/**
 * Модель расписания.
 */
class Schedule {

    private val weeks = linkedMapOf<DayOfWeek, ScheduleDay>()

    init {
        for (dayOfWeek in DayOfWeek.values()) {
            weeks[dayOfWeek] = ScheduleDay()
        }
    }

    /**
     * Добавляет пару в расписание.
     */
    fun add(pair: Pair) {
        weeks[pair.date.dayOfWeek()]!!.add(pair)
    }

    /**
     * Удаляет пару из расписания.
     */
    fun remove(pair: Pair?) {
        if (pair == null) {
            return
        }

        weeks[pair.date.dayOfWeek()]!!.remove(pair)
    }

    /**
     * Возвращает дату, с которого начинается расписание.
     * Если расписание пустое, то возвращается null.
     */
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

    /**
     * Возвращает дату, на которую заканчивается расписание.
     * Если расписание пустое, то возвращается null.
     */
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

    /**
     * Ограничивает дату, исходя из дат начала и конца расписания.
     */
    fun limitDate(date: LocalDate) : LocalDate {
        startDate()?.let {
            if (date.isBefore(it)) {
                return it
            }
        }

        endDate()?.let {
            if (date.isAfter(it)) {
                return it
            }
        }

        return date
    }

    /**
     * Проверяет, является ли расписание пустым.
     */
    fun isEmpty() : Boolean {
        return startDate() == null || endDate() == null
    }

    /**
     * Возвращает список пар, которые есть в заданный день.
     */
    fun pairsByDate(date: LocalDate): List<Pair> {
        if (date.dayOfWeek == DateTimeConstants.SUNDAY) {
            return arrayListOf()
        }
        val dayOfWeek = DayOfWeek.of(date)
        return weeks[dayOfWeek]!!.pairsByDate(date)
    }

    /**
     * Проверяет, можно ли заменить одну пару на другую.
     */
    private fun possibleChangePair(old: Pair?, new: Pair) {
        weeks[new.date.dayOfWeek()]!!.possibleChangePair(old, new)
    }

    /**
     * Заменяет одну пару на другую.
     */
    fun changePair(old: Pair?, new: Pair) {
        possibleChangePair(old, new)

        remove(old)
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