package com.vereshchagin.nikolay.stankinschedule.schedule.domain.model


import com.vereshchagin.nikolay.stankinschedule.model.schedule.pair.DayOfWeek
import org.joda.time.DateTimeConstants
import org.joda.time.LocalDate

/**
 * Модель расписания.
 */
class ScheduleModel(val info: ScheduleInfo) : Iterable<PairModel> {

    /**
     * Контейнер дней в расписании.
     */
    private val days = linkedMapOf<DayOfWeek, ScheduleDayModel>()

    /**
     * Добавляет пару в расписание.
     */
    fun add(pair: PairModel) {
        dayFor(pair).add(pair)
    }

    /**
     * Удаляет пару из расписания.
     */
    fun remove(pair: PairModel) {
        dayFor(pair).remove(pair)
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

    /**
     * Возвращает список всех дисциплин в расписании.
     */
    fun disciplines(): List<String> {
        val disciplines = mutableSetOf<String>()
        for (day in days.values) {
            for (pair in day) {
                disciplines.add(pair.title)
            }
        }

        return disciplines.sorted()
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
    fun pairsByDate(date: LocalDate): List<PairModel> {
        if (date.dayOfWeek == DateTimeConstants.SUNDAY) {
            return arrayListOf()
        }
        val dayOfWeek = DayOfWeek.of(date)
        return days[dayOfWeek]!!.pairsByDate(date)
    }

    /**
     * Возвращает список всех пар по названию дисциплины.
     */
    fun pairsByDiscipline(discipline: String): List<PairModel> {
        val pairs = arrayListOf<PairModel>()
        for (day in days.values) {
            pairs.addAll(day.pairsByDiscipline(discipline))
        }
        return pairs
    }

    /**
     * Проверяет, можно ли заменить одну пару на другую.
     */
    fun possibleChangePair(old: PairModel?, new: PairModel) {
        dayFor(new).possibleChangePair(old, new)
    }

    /**
     * Заменяет одну пару на другую.
     */
    fun changePair(old: PairModel?, new: PairModel) {
        possibleChangePair(old, new)

        if (old != null) {
            remove(old)
        }
        add(new)
    }

    /**
     * Возвращает день расписания для пары.
     */
    private fun dayFor(pair: PairModel): ScheduleDayModel {
        return days.getOrPut(pair.date.dayOfWeek()) { ScheduleDayModel() }
    }

    override fun iterator(): Iterator<PairModel> = days.values.flatten().iterator()
}