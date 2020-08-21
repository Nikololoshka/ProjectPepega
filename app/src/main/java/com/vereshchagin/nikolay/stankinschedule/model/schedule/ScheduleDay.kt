package com.vereshchagin.nikolay.stankinschedule.model.schedule

import com.vereshchagin.nikolay.stankinschedule.model.schedule.pair.Pair
import com.vereshchagin.nikolay.stankinschedule.model.schedule.pair.PairIntersectException
import org.joda.time.LocalDate


class ScheduleDay {

    val pairs = linkedSetOf<Pair>()

    fun add(pair: Pair) {
        isAddCheck(pair)
        pairs.add(pair)
    }

    fun remove(pair: Pair) {
        pairs.remove(pair)
    }

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

    private fun isAddCheck(added: Pair) {
        for (pair in pairs) {
            if (added.intersect(pair) && !added.separate(pair)) {
                throw PairIntersectException(
                    "There can't be two pairs at the same time: '$pair' and '$added'",
                    pair,
                    added
                )
            }
        }
    }

    fun possibleChangePair(old: Pair?, new: Pair) {
        for (pair in pairs) {
            if (pair != old) {
                if (new.intersect(pair) && !new.separate(pair)) {
                    throw PairIntersectException(
                        "There can't be two pairs at the same time: '$pair' and '$new'",
                        pair,
                        new
                    )
                }
            }
        }
    }

    fun pairsByDate(date: LocalDate): ArrayList<Pair> {
        val pairsDate = ArrayList<Pair>()
        for (pair in pairs) {
            if (pair.date.intersect(date)) {
                pairsDate.add(pair)
            }
        }
        return pairsDate
    }
}