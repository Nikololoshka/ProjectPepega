package com.vereshchagin.nikolay.stankinschedule.model.schedule

import com.vereshchagin.nikolay.stankinschedule.model.schedule.db.PairItem
import com.vereshchagin.nikolay.stankinschedule.model.schedule.db.ScheduleItem
import com.vereshchagin.nikolay.stankinschedule.model.schedule.db.ScheduleWithPairs
import com.vereshchagin.nikolay.stankinschedule.model.schedule.pair.DayOfWeek
import com.vereshchagin.nikolay.stankinschedule.model.schedule.pair.Pair
import org.joda.time.DateTimeConstants
import org.joda.time.LocalDate

/**
 *
 */
class Schedule(
    scheduleWithPairs: ScheduleWithPairs,
) {
    /**
     *
     */
    val info = scheduleWithPairs.schedule

    /**
     *
     */
    private val days = linkedMapOf<DayOfWeek, ScheduleDay>()

    init {
        for (dayOfWeek in DayOfWeek.values()) {
            days[dayOfWeek] = ScheduleDay()
        }

        for (pair in scheduleWithPairs.pairs) {
            add(pair)
        }
    }

    /**
     * Добавляет пару в расписание.
     */
    fun add(pair: PairItem) {
        days[pair.date.dayOfWeek()]!!.add(pair)
    }

    /**
     * Удаляет пару из расписания.
     */
    fun remove(pair: PairItem) {
        days[pair.date.dayOfWeek()]!!.remove(pair)
    }

    /**
     * Возвращает дату, с которого начинается расписание.
     * Если расписание пустое, то возвращается null.
     */
    fun startDate(): LocalDate? {
        var start: LocalDate? = null

        for (day in days.values) {
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

        for (day in days.values) {
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

    fun disciplines(): List<String> {
        val disciplines = HashSet<String>()
        for (day in days.values) {
            for (pair in day.pairs) {
                disciplines.add(pair.title)
            }
        }

        return disciplines.toList()
    }

    /**
     * Ограничивает дату, исходя из дат начала и конца расписания.
     */
    fun limitDate(date: LocalDate): LocalDate {
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
    fun isEmpty(): Boolean {
        return startDate() == null || endDate() == null
    }

    /**
     * Возвращает список пар, которые есть в заданный день.
     */
    fun pairsByDate(date: LocalDate): List<PairItem> {
        if (date.dayOfWeek == DateTimeConstants.SUNDAY) {
            return arrayListOf()
        }
        val dayOfWeek = DayOfWeek.of(date)
        return days[dayOfWeek]!!.pairsByDate(date)
    }

    fun pairsByDiscipline(discipline: String): List<PairItem> {
        val pairs = arrayListOf<PairItem>()
        for (day in days.values) {
            pairs.addAll(day.pairsByDiscipline(discipline))
        }
        return pairs
    }

    /**
     * Проверяет, можно ли заменить одну пару на другую.
     */
    fun possibleChangePair(old: Pair?, new: Pair) {
        days[new.date.dayOfWeek()]!!.possibleChangePair(old, new)
    }

    /**
     * Заменяет одну пару на другую.
     */
    fun changePair(old: PairItem?, new: PairItem) {
        possibleChangePair(old, new)

        if (old != null) {
            remove(old)
        }
        add(new)
    }

    companion object {
        fun empty(scheduleName: String = "test"): Schedule {
            return Schedule(
                ScheduleWithPairs(ScheduleItem(scheduleName), emptyList())
            )
        }
    }
}