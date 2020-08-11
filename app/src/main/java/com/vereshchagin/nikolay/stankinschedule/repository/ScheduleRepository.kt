package com.vereshchagin.nikolay.stankinschedule.repository

import com.google.gson.GsonBuilder
import com.vereshchagin.nikolay.stankinschedule.model.schedule.Schedule
import com.vereshchagin.nikolay.stankinschedule.model.schedule.pair.Pair
import org.apache.commons.io.FileUtils
import java.io.File
import java.nio.charset.StandardCharsets


class ScheduleRepository {

    private val gson = GsonBuilder()
        .registerTypeAdapter(Schedule::class.java, Schedule.Serializer())
        .registerTypeAdapter(Pair::class.java, Pair.Serializer())
        .create()

    fun load(path: String): Schedule {
        val json = FileUtils.readFileToString(File(path), StandardCharsets.UTF_8)
        return gson.fromJson(json, Schedule::class.java)
    }

    fun save(schedule: Schedule, path: String) {
        val json = gson.toJson(schedule)
        FileUtils.writeStringToFile(File(path), json, StandardCharsets.UTF_8)
    }
}