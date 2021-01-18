package com.vereshchagin.nikolay.stankinschedule

import com.google.gson.GsonBuilder
import com.vereshchagin.nikolay.stankinschedule.model.schedule.Schedule
import com.vereshchagin.nikolay.stankinschedule.model.schedule.pair.Pair
import com.vereshchagin.nikolay.stankinschedule.model.schedule.pair.PairIntersectException
import com.vereshchagin.nikolay.stankinschedule.resources.PairResources
import org.junit.Before
import org.junit.Test

/**
 * Тесты для расписания.
 */
class ScheduleTest {

    private val pairs: ArrayList<Pair> = arrayListOf()

    @Before
    fun uploadPairs() {
        val gson = GsonBuilder()
            .registerTypeAdapter(Pair::class.java, Pair.Deserializer())
            .create()

        for (json in PairResources.PAIRS) {
            pairs.add(gson.fromJson(json, Pair::class.java))
        }
    }

    /**
     * Добавление в пустое расписание пары.
     */
    @Test
    fun loading() {
        val schedule = Schedule()
        schedule.add(pairs[0])
    }

    /**
     * Невозможное существование пар по дате.
     */
    @Test(expected = PairIntersectException::class)
    fun impossibleDatePairs() {
        val schedule = Schedule()
        schedule.add(pairs[0])
        schedule.add(pairs[1])
    }

    /**
     * Возможное существование пар в расписании по времени.
     */
    @Test
    fun possibleTimePairs() {
        val schedule = Schedule()
        schedule.add(pairs[1])
        schedule.add(pairs[2])
    }

    /**
     * Невозможное существование пары по подгруппе.
     */
    @Test(expected = PairIntersectException::class)
    fun impossibleSubgroupIntersect() {
        val schedule = Schedule()
        schedule.add(pairs[3])
        schedule.add(pairs[4])
        schedule.add(pairs[5])
    }
}