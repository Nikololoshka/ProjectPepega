package com.vereshchagin.nikolay.stankinschedule

import com.google.gson.GsonBuilder
import com.vereshchagin.nikolay.stankinschedule.model.schedule.Schedule
import com.vereshchagin.nikolay.stankinschedule.model.schedule.pair.Pair
import com.vereshchagin.nikolay.stankinschedule.model.schedule.pair.PairIntersectException
import com.vereshchagin.nikolay.stankinschedule.repository.ScheduleRepository
import org.apache.commons.io.FileUtils
import org.junit.Assert
import org.junit.Test
import java.io.File
import java.io.IOException
import java.nio.charset.StandardCharsets

/**
 * Тесты связанные с расписанием.
 */
class ScheduleTest {
    @Test
    fun loading() {
        val schedule = Schedule()
        schedule.add(loadPair("pair_1.json"))
        Assert.assertTrue(true)
    }

    @Test(expected = PairIntersectException::class)
    fun impossiblePairs() {
        val schedule = Schedule()
        schedule.add(loadPair("pair_1.json"))
        schedule.add(loadPair("pair_2.json"))
    }

    @Test
    fun possiblePairs() {
        val schedule = Schedule()
        schedule.add(loadPair("pair_2.json"))
        schedule.add(loadPair("pair_3.json"))
    }

    @Test(expected = PairIntersectException::class)
    fun impossibleIntersect() {
        val schedule = Schedule()
        schedule.add(loadPair("pair_4.json"))
        schedule.add(loadPair("pair_5.json"))
        schedule.add(loadPair("pair_6.json"))
    }

    @Test
    fun commonLoading() {
        val schedules = FileUtils.listFiles(File(PATH_SCHEDULES), null, false)
        for (schedule in schedules) {
            println(schedule.name)

//            if (schedule.name == "МДС-18-02.json") {
//                continue
//            }

            val json = FileUtils.readFileToString(schedule, StandardCharsets.UTF_8)
            GsonBuilder()
                .registerTypeAdapter(Schedule::class.java, Schedule.Deserializer())
                .registerTypeAdapter(Pair::class.java, Pair.Deserializer())
                .create()
                .fromJson(json, Schedule::class.java)
        }
    }

    @Test
    fun repositoryLoading() {
        val schedules = FileUtils.listFiles(File(PATH_SCHEDULES), null, false)

        val repository = ScheduleRepository()
        for (schedule in schedules) {
            println(schedule)

//            if (schedule.name == "МДС-18-02.json") {
//                continue
//            }

            repository.load(schedule.absolutePath)
        }
    }

    /**
     * Загружает пару из файла.
     * @param filename имя файла.
     * @return пара.
     */
    private fun loadPair(filename: String): Pair {
        try {

            val file = FileUtils.getFile(PATH, filename)
            val json = FileUtils.readFileToString(file, StandardCharsets.UTF_8)

            return GsonBuilder()
                .registerTypeAdapter(Pair::class.java, Pair.Deserializer())
                .create()
                .fromJson(json, Pair::class.java)

        } catch (e: IOException) {
            throw IllegalArgumentException("Unknown error", e)
        }
    }

    companion object {
        private const val PATH = "src/test/resources/"
        private const val PATH_SCHEDULES = "src/main/assets/schedules/"
    }
}