package com.vereshchagin.nikolay.stankinschedule

import com.google.gson.GsonBuilder
import com.vereshchagin.nikolay.stankinschedule.model.schedule.Schedule
import com.vereshchagin.nikolay.stankinschedule.model.schedule.ScheduleResponse
import com.vereshchagin.nikolay.stankinschedule.model.schedule.db.PairItem
import com.vereshchagin.nikolay.stankinschedule.model.schedule.pair.Pair
import com.vereshchagin.nikolay.stankinschedule.model.schedule.pair.PairIntersectException
import com.vereshchagin.nikolay.stankinschedule.resources.PairResources
import org.apache.commons.io.FileUtils
import org.junit.Before
import org.junit.Test
import java.io.File
import java.nio.charset.StandardCharsets

/**
 * Тесты для расписания.
 */
class ScheduleTest {

    private val pairs: ArrayList<PairItem> = arrayListOf()

    @Before
    fun uploadPairs() {
        val gson = GsonBuilder()
            .registerTypeAdapter(Pair::class.java, Pair.Serializer())
            .create()

        for (json in PairResources.PAIRS) {
            pairs.add(gson.fromJson(json, Pair::class.java).toPairItem(-1))
        }
    }

    /**
     * Добавление в пустое расписание пары.
     */
    @Test
    fun loading() {
        val schedule = Schedule.empty()
        schedule.add(pairs[0])
    }

    /**
     * Невозможное существование пар по дате.
     */
    @Test(expected = PairIntersectException::class)
    fun impossibleDatePairs() {
        val schedule = Schedule.empty()
        schedule.add(pairs[0])
        schedule.add(pairs[1])
    }

    /**
     * Возможное существование пар в расписании по времени.
     */
    @Test
    fun possibleTimePairs() {
        val schedule = Schedule.empty()
        schedule.add(pairs[1])
        schedule.add(pairs[2])
    }

    /**
     * Невозможное существование пары по подгруппе.
     */
    @Test(expected = PairIntersectException::class)
    fun impossibleSubgroupIntersect() {
        val schedule = Schedule.empty()
        schedule.add(pairs[3])
        schedule.add(pairs[4])
        schedule.add(pairs[5])
    }

    @Test
    fun tempStressTest() {
        val path = "J:/data/schedules-json/"
        val schedules = FileUtils.listFiles(File(path), null, false)

        for (schedule in schedules) {
            println(schedule.name)

            val json = FileUtils.readFileToString(schedule, StandardCharsets.UTF_8)
            GsonBuilder()
                .registerTypeAdapter(ScheduleResponse::class.java, ScheduleResponse.Serializer())
                .registerTypeAdapter(Pair::class.java, Pair.Serializer())
                .create()
                .fromJson(json, ScheduleResponse::class.java)
        }
    }
}