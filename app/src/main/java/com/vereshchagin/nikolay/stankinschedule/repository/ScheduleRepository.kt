package com.vereshchagin.nikolay.stankinschedule.repository

import android.content.Context
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import com.vereshchagin.nikolay.stankinschedule.model.schedule.Schedule
import com.vereshchagin.nikolay.stankinschedule.model.schedule.pair.Pair
import com.vereshchagin.nikolay.stankinschedule.ui.settings.SchedulePreference
import org.apache.commons.io.FileUtils
import java.io.File
import java.io.IOException
import java.nio.charset.StandardCharsets


/**
 * Репозиторий для работы с расписанием.
 */
class ScheduleRepository {

    private val gson = GsonBuilder()
        .registerTypeAdapter(Schedule::class.java, Schedule.Serializer())
        .registerTypeAdapter(Pair::class.java, Pair.Serializer())
        .registerTypeAdapter(Schedule::class.java, Schedule.Deserializer())
        .registerTypeAdapter(Pair::class.java, Pair.Deserializer())
        .create()

    /**
     * Загружает расписание.
     */
    @Throws(IOException::class, JsonSyntaxException::class)
    fun load(path: String): Schedule {
        val json = FileUtils.readFileToString(File(path), StandardCharsets.UTF_8)
        return gson.fromJson(json, Schedule::class.java)
    }

    /**
     * Возвращает список расписаний устройста.
     */
    fun schedules(context: Context): List<String> {
        return SchedulePreference.schedules(context)
    }

    /**
     * Создает расписание.
     */
    fun createSchedule(context: Context, scheduleName: String) {
        val schedule = Schedule()
        val path = SchedulePreference.createPath(context, scheduleName)
        save(schedule, path)
        SchedulePreference.add(context, scheduleName)
    }

    /**
     * Переименновывает расписание.
     */
    fun renameSchedule(context: Context, oldName: String, newName: String) {
        val oldFile = File(SchedulePreference.createPath(context, oldName))
        val newFile = File(SchedulePreference.createPath(context, newName))

        FileUtils.moveFile(oldFile, newFile)

        // если удалось переименовать расписание
        SchedulePreference.remove(context, oldName)
        SchedulePreference.add(context, newName)

        if (oldName == SchedulePreference.favorite(context)) {
            SchedulePreference.setFavorite(context, newName)
        }
    }

    /**
     * Удаляет расписание.
     */
    fun removeSchedule(context: Context, scheduleName: String) {
        val path = SchedulePreference.createPath(context, scheduleName)
        if (FileUtils.deleteQuietly(File(path))) {
            SchedulePreference.remove(context, scheduleName)
        }
    }

    /**
     * Сохраняет расписание.
     */
    fun save(schedule: Schedule, path: String) {
        val json = gson.toJson(schedule)
        FileUtils.writeStringToFile(File(path), json, StandardCharsets.UTF_8)
    }

    /**
     * Сохраняет расписание.
     */
    fun saveNew(context: Context, schedule: Schedule, scheduleName: String) {
        if (schedules(context).contains(scheduleName)) {
            throw FileAlreadyExistsException(File(scheduleName))
        }

        val path = SchedulePreference.createPath(context, scheduleName)
        save(schedule, path)
        SchedulePreference.add(context, scheduleName)
    }

    /**
     * Создает путь к расписанию.
     */
    fun path(context: Context, scheduleName: String) : String {
        return SchedulePreference.createPath(context, scheduleName)
    }

    /**
     * Проверяет, существует ли расписание с таким именем.
     */
    fun exists(context: Context, scheduleName: String): Boolean {
        return schedules(context).contains(scheduleName)
    }

    /**
     * Загружает и сохранаяет расписание с json.
     */
    fun loadAndSaveFromJson(context: Context, json: String, scheduleName: String) {
        if (schedules(context).contains(scheduleName)) {
            throw FileAlreadyExistsException(File(scheduleName))
        }

        val schedule = gson.fromJson(json, Schedule::class.java)
        val path = SchedulePreference.createPath(context, scheduleName)
        save(schedule, path)

        SchedulePreference.add(context, scheduleName)
    }
}