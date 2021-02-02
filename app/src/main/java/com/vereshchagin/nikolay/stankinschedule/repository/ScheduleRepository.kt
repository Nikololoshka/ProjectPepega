package com.vereshchagin.nikolay.stankinschedule.repository

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import com.vereshchagin.nikolay.stankinschedule.model.schedule.Schedule
import com.vereshchagin.nikolay.stankinschedule.model.schedule.pair.Pair
import com.vereshchagin.nikolay.stankinschedule.settings.SchedulePreference
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
     * Загружает расписание по имени.
     */
    @Throws(IOException::class, JsonSyntaxException::class)
    fun load(scheduleName: String, context: Context): Schedule {
        val path = path(context, scheduleName)
        return load(path)
    }

    /**
     * Возвращает список расписаний устройства.
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
     * Переименовывает расписание.
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
     * Копирует расписание по необходимому пути.
     */
    @Suppress("UNUSED_PARAMETER")
    fun copy(scheduleName: String, schedule: Schedule, uri: Uri, context: Context) {
        // получаем объект файла по пути
        var documentFile: DocumentFile? = DocumentFile.fromTreeUri(context, uri)

        // регистрируем файл
        documentFile = documentFile?.createFile(
            "application/json",
            scheduleName + SchedulePreference.fileExtension()
        )
        if (documentFile == null) {
            throw RuntimeException("Failed register file on device")
        }

        // uri файла сохранения
        val uriFile = documentFile.uri

        // открывает поток для записи
        val resolver = context.contentResolver
        val stream = resolver.openOutputStream(uriFile)
            ?: throw RuntimeException("Cannot open file stream")

        FileUtils.copyFile(
            File(path(context, scheduleName)),
            stream
        )
    }

    /**
     * Возвращает избранное расписание
     */
    fun favorite(context: Context): String? {
        val scheduleName: String? = SchedulePreference.favorite(context)
        if (scheduleName == null || scheduleName.isEmpty()) {
            return null
        }
        return scheduleName
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
    fun path(context: Context, scheduleName: String): String {
        return SchedulePreference.createPath(context, scheduleName)
    }

    /**
     * Проверяет, существует ли расписание с таким именем.
     */
    fun exists(context: Context, scheduleName: String): Boolean {
        return schedules(context).contains(scheduleName)
    }

    /**
     * Загружает и сохраняет расписание с json.
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