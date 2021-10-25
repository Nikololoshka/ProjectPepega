package com.vereshchagin.nikolay.stankinschedule.repository

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.vereshchagin.nikolay.stankinschedule.db.MainApplicationDatabase
import com.vereshchagin.nikolay.stankinschedule.db.dao.ScheduleDao
import com.vereshchagin.nikolay.stankinschedule.model.schedule.Schedule
import com.vereshchagin.nikolay.stankinschedule.model.schedule.ScheduleResponse
import com.vereshchagin.nikolay.stankinschedule.model.schedule.db.PairItem
import com.vereshchagin.nikolay.stankinschedule.model.schedule.db.ScheduleItem
import com.vereshchagin.nikolay.stankinschedule.model.schedule.pair.Pair
import com.vereshchagin.nikolay.stankinschedule.settings.SchedulePreference
import com.vereshchagin.nikolay.stankinschedule.settings.SchedulePreferenceKt
import com.vereshchagin.nikolay.stankinschedule.utils.extensions.extractFilename
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.apache.commons.io.FileUtils
import java.io.File
import java.io.FileNotFoundException
import java.nio.charset.StandardCharsets
import javax.inject.Inject

/**
 * Репозиторий для работы с расписаниями на устройстве.
 * @param db база данных приложения.
 * @param dao интерфейс для работы с БД.
 * @param preference настройки расписания.
 */
class ScheduleRepository @Inject constructor(
    private val db: MainApplicationDatabase,
    private val dao: ScheduleDao,
    private val preference: SchedulePreferenceKt,
) {
    /**
     * Gson для преобразования расписания в/из JSON.
     */
    val gson: Gson = GsonBuilder()
        .registerTypeAdapter(ScheduleResponse::class.java, ScheduleResponse.Serializer())
        .registerTypeAdapter(Pair::class.java, Pair.Serializer())
        .create()

    /**
     * ID избранного расписания.
     */
    var favoriteScheduleId
        get() = preference.favoriteScheduleId
        set(value) {
            preference.favoriteScheduleId = value
        }

    /**
     * Возвращает flow списка всех расписаний на устройстве.
     */
    fun schedules(): Flow<List<ScheduleItem>> = dao.getAllSchedules()

    /**
     * Возвращает flow расписания по названию.
     */
    fun schedule(scheduleName: String): Flow<Schedule?> = dao.getScheduleWithPairs(scheduleName)
        .map {
            if (it != null) Schedule(it) else null
        }

    /**
     * Возвращает flow расписания по ID.
     */
    fun schedule(id: Long): Flow<Schedule?> = dao.getScheduleWithPairs(id)
        .map {
            if (it != null) Schedule(it) else null
        }

    /**
     * Возвращает flow информации о расписании по названию.
     */
    fun scheduleItem(scheduleName: String) = dao.getScheduleItem(scheduleName)

    /**
     * Возвращает flow информации о расписании по ID.
     */
    fun scheduleItem(scheduleId: Long) = dao.getScheduleItem(scheduleId)

    /**
     * Создает пустое расписание с заданным именем.
     */
    suspend fun createSchedule(scheduleName: String) {
        dao.insertScheduleItem(ScheduleItem(scheduleName))
    }

    /**
     * Обновляет информацию о расписание в БД.
     */
    suspend fun updateScheduleItem(item: ScheduleItem) = dao.updateScheduleItem(item)

    /**
     * Обновляет информацию о расписаниях из списка в БД.
     */
    suspend fun updateScheduleItems(schedules: List<ScheduleItem>) =
        dao.updateScheduleItems(schedules)

    /**
     * Удаляет расписание из БД.
     */
    suspend fun removeSchedule(scheduleName: String) = dao.deleteSchedule(scheduleName)

    /**
     * Удаляет расписание из БД.
     */
    suspend fun removeSchedule(scheduleId: Long) = dao.deleteSchedule(scheduleId)

    /**
     * Проверяет, существует ли расписание с переданным названием.
     */
    suspend fun isScheduleExist(scheduleName: String) = dao.isScheduleExist(scheduleName)

    /**
     * Возвращает flow пары по ID.
     */
    fun pair(id: Long): Flow<PairItem?> = dao.getPairItem(id)

    /**
     * Обновляет пару в БД.
     */
    suspend fun updatePair(pair: PairItem) = dao.insertPairItem(pair)

    /**
     * Удаляет пару из БД.
     */
    suspend fun removePair(pair: PairItem) = dao.deletePairItem(pair)

    /**
     * Изменяет состояние синхронизации расписания.
     */
    suspend fun toggleScheduleSyncState(scheduleId: Long, sync: Boolean) {
        val item = scheduleItem(scheduleId).first()
        if (item != null) {
            item.synced = sync
            if (!sync) item.lastUpdate = null
            updateScheduleItem(item)
        }
    }

    /**
     * Возвращает ScheduleResponse расписания из БД по названию.
     */
    private suspend fun scheduleResponse(scheduleId: Long): ScheduleResponse {
        return ScheduleResponse(dao.getAllPairs(scheduleId).first())
    }

    /**
     * Сохраняет расписание на устройство.
     */
    @Throws(RuntimeException::class)
    suspend fun saveToDevice(scheduleId: Long, uri: Uri, context: Context) {
        val item = scheduleItem(scheduleId).first()
            ?: throw RuntimeException("Schedule not found: ID - $scheduleId")

        // получаем объект файла по пути
        var documentFile: DocumentFile? = DocumentFile.fromTreeUri(context, uri)

        // регистрируем файл
        documentFile = documentFile?.createFile("application/json", "${item.scheduleName}.json")
        if (documentFile == null) {
            throw RuntimeException("Failed register file on device")
        }

        // uri файла сохранения
        val uriFile = documentFile.uri

        // открывает поток для записи
        val resolver = context.contentResolver

        val stream = runCatching {
            resolver.openOutputStream(uriFile)
        }.getOrNull() ?: throw RuntimeException("Cannot open file stream")

        val response = scheduleResponse(scheduleId)
        stream.bufferedWriter().use {
            gson.toJson(response, it)
        }
    }

    /**
     * Загружает расписание с устройства.
     */
    @Throws(RuntimeException::class)
    suspend fun loadFromDevice(uri: Uri, context: Context) {
        val scheduleName = uri.extractFilename(context) ?: throw FileNotFoundException()
        val resolver = context.contentResolver

        val response = runCatching {
            resolver.openInputStream(uri)
        }.getOrNull()?.bufferedReader().use { reader ->
            gson.fromJson(reader, ScheduleResponse::class.java)
        } ?: throw RuntimeException("Cannot load json")

        saveResponse(scheduleName, response)
    }

    /**
     * Сохраняет ScheduleResponse в БД.
     */
    suspend fun saveResponse(
        scheduleName: String,
        response: ScheduleResponse,
        replaceExist: Boolean = false,
        isSync: Boolean = false,
    ) {
        dao.insertScheduleResponse(scheduleName, response, replaceExist, isSync)
    }


    companion object {
        /**
         * ID расписания, если его нет.
         */
        const val NO_SCHEDULE = -1L


        /**
         * Осуществляет миграцию расписаний из internal хранилища с расписаниями в БД.
         */
        @Suppress("DEPRECATION")
        suspend fun migrateSchedules(context: Context, db: MainApplicationDatabase) {
            val schedules = SchedulePreference.schedules(context)
            for (scheduleName in schedules) {
                val path = SchedulePreference.createPath(context, scheduleName)

                try {
                    val json = FileUtils.readFileToString(File(path), StandardCharsets.UTF_8)
                    val gson = GsonBuilder()
                        .registerTypeAdapter(
                            ScheduleResponse::class.java,
                            ScheduleResponse.Serializer()
                        )
                        .registerTypeAdapter(Pair::class.java, Pair.Serializer())
                        .create()

                    val response = gson.fromJson(json, ScheduleResponse::class.java)
                    db.schedules().insertScheduleResponse(scheduleName, response)

                } catch (ignored: Exception) {

                }
            }
        }
    }
}