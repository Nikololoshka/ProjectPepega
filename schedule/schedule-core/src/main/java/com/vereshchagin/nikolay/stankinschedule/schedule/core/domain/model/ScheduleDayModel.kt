package com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model

import org.joda.time.LocalDate

/**
 * День в расписании.
 */
class ScheduleDayModel : Iterable<PairModel> {

    /**
     * Пары в дне.
     */
    private val pairs = arrayListOf<PairModel>()

    /**
     * Добавляет пару в день.
     */
    fun add(pair: PairModel) {
        isAddCheck(pair)
        pairs.add(pair)
    }

    /**
     * Удаляет пару.
     */
    fun remove(pair: PairModel) {
        pairs.removeIf { it == pair }
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
    private fun isAddCheck(added: PairModel) {
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
    fun possibleChangePair(old: PairModel?, new: PairModel) {
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
    fun pairsByDate(date: LocalDate): List<PairModel> {
        val pairsDate = ArrayList<PairModel>()
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
    fun pairsByDiscipline(discipline: String): List<PairModel> {
        val result = arrayListOf<PairModel>()
        for (pair in pairs) {
            if (pair.title == discipline) {
                result.add(pair)
            }
        }
        return result
    }

    override fun iterator(): Iterator<PairModel> = pairs.iterator()
}