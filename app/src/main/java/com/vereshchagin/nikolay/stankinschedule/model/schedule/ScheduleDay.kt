package com.vereshchagin.nikolay.stankinschedule.model.schedule

import com.vereshchagin.nikolay.stankinschedule.model.schedule.pair.Pair


class ScheduleDay {

    val pairs = linkedSetOf<Pair>()

    fun add(pair: Pair) {
        isAddCheck(pair)
        pairs.add(pair)
    }

    fun remove(pair: Pair) {
        pairs.remove(pair)
    }

    private fun isAddCheck(added: Pair) {
        for (pair in pairs) {
            if (added.intersect(pair) && !added.separate(pair)) {
                throw IllegalArgumentException(
                    "There can't be two pairs at the same time: '$pair' and '$added'"
                )
            }
        }
    }
}