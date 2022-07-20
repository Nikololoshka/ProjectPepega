package com.vereshchagin.nikolay.stankinschedule.model.schedule

import com.vereshchagin.nikolay.stankinschedule.model.schedule.db.PairItem
import com.vereshchagin.nikolay.stankinschedule.model.schedule.db.ScheduleItem
import com.vereshchagin.nikolay.stankinschedule.model.schedule.db.ScheduleWithPairs
import com.vereshchagin.nikolay.stankinschedule.model.schedule.pair.DayOfWeek
import com.vereshchagin.nikolay.stankinschedule.utils.extensions.removeIfJava7
import org.joda.time.DateTimeConstants
import org.joda.time.LocalDate

/**
 * Модель расписания.
 */
class Schedule(
    scheduleWithPairs: ScheduleWithPairs,
) {
    /**
     * Информация о расписании (хранящиеся в БД)
     */
    val info = scheduleWithPairs.schedule

    /**
     * Контейнер дней в расписании.
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

    /**
     * Возвращает список всех дисциплин в расписании.
     */
    fun disciplines(): List<String> {
        val disciplines = mutableSetOf<String>()
        for (day in days.values) {
            for (pair in day.pairs) {
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
    fun pairsByDate(date: LocalDate): List<PairItem> {
        if (date.dayOfWeek == DateTimeConstants.SUNDAY) {
            return arrayListOf()
        }
        val dayOfWeek = DayOfWeek.of(date)
        return days[dayOfWeek]!!.pairsByDate(date)
    }

    /**
     * Возвращает список всех пар по названию дисциплины.
     */
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
    fun possibleChangePair(old: PairItem?, new: PairItem) {
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

        /**
         * Возвращает пустое расписание.
         */
        fun empty(scheduleName: String = "empty"): Schedule {
            return Schedule(
                ScheduleWithPairs(ScheduleItem(scheduleName), emptyList())
            )
        }
    }


    /**
     * День в расписании.
     */
    class ScheduleDay {

        /**
         * Пары в дне.
         */
        internal val pairs = arrayListOf<PairItem>()

        /**
         * Добавляет пару в день.
         */
        fun add(pair: PairItem) {
            isAddCheck(pair)
            pairs.add(pair)
        }

        /**
         * Удаляет пару.
         */
        fun remove(pair: PairItem) {
            pairs.removeIfJava7 {
                it == pair
            }
        }

        /**
         * Возвращает дату, с которого начинается расписание.
         * Если расписание пустое, то возвращается null.
         */
        fun startDate(): LocalDate? {
            if (pairs.isEmpty()) {
                return null
            }

            var first: LocalDate? = null
            for (pair in pairs) {
                val firstPair = pair.date.startDate()
                if (first != null) {
                    if (firstPair != null && firstPair < first) {
                        first = firstPair
                    }
                } else {
                    first = firstPair
                }
            }

            return first
        }

        /**
         * Возвращает дату, на которую заканчивается расписание.
         * Если расписание пустое, то возвращается null.
         */
        fun endDate(): LocalDate? {
            if (pairs.isEmpty()) {
                return null
            }

            var last: LocalDate? = null
            for (pair in pairs) {
                val lastPair = pair.date.endDate()
                if (last != null) {
                    if (lastPair != null && lastPair > last) {
                        last = lastPair
                    }
                } else {
                    last = lastPair
                }
            }

            return last
        }

        /**
         * Проверяет, можно ли добавить пару в расписание.
         */
        @Throws(PairIntersectException::class)
        private fun isAddCheck(added: PairItem) {
            for (pair in pairs) {
                if (added.isIntersect(pair)) {
                    throw PairIntersectException(
                        "There can't be two pairs at the same time: '$pair' and '$added'",
                        pair,
                        added
                    )
                }
            }
        }

        /**
         * Проверяет, можно ли заменить пару в расписании.
         */
        @Throws(PairIntersectException::class)
        fun possibleChangePair(old: PairItem?, new: PairItem) {
            for (pair in pairs) {
                if (pair != old && new.isIntersect(pair)) {
                    throw PairIntersectException(
                        "There can't be two pairs at the same time: '$pair' and '$new'",
                        pair,
                        new
                    )
                }
            }
        }

        /**
         * Возвращает список пар, которые есть в заданный день.
         */
        fun pairsByDate(date: LocalDate): List<PairItem> {
            val pairsDate = ArrayList<PairItem>()
            for (pair in pairs) {
                if (pair.date.intersect(date)) {
                    pairsDate.add(pair)
                }
            }
            return pairsDate.sorted()
        }

        /**
         * Возвращает список всех пар по названию дисциплины.
         */
        fun pairsByDiscipline(discipline: String): List<PairItem> {
            val result = arrayListOf<PairItem>()
            for (pair in pairs) {
                if (pair.title == discipline) {
                    result.add(pair)
                }
            }
            return result
        }
    }
}