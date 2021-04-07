package com.vereshchagin.nikolay.stankinschedule.repository

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import com.google.gson.GsonBuilder
import com.vereshchagin.nikolay.stankinschedule.db.MainApplicationDatabase
import com.vereshchagin.nikolay.stankinschedule.model.schedule.Schedule
import com.vereshchagin.nikolay.stankinschedule.model.schedule.ScheduleResponse
import com.vereshchagin.nikolay.stankinschedule.model.schedule.db.PairItem
import com.vereshchagin.nikolay.stankinschedule.model.schedule.db.ScheduleItem
import com.vereshchagin.nikolay.stankinschedule.model.schedule.pair.Pair
import com.vereshchagin.nikolay.stankinschedule.settings.SchedulePreference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.apache.commons.io.FileUtils
import java.io.File
import java.io.FileNotFoundException
import java.nio.charset.StandardCharsets

/**
 * Репозиторий для работы с расписаниями на устройстве.
 */
class ScheduleRepository(
    context: Context,
) {
    /**
     * Gson для преобразования расписания в/из JSON.
     */
    val gson = GsonBuilder()
        .registerTypeAdapter(ScheduleResponse::class.java, ScheduleResponse.Serializer())
        .registerTypeAdapter(Pair::class.java, Pair.Serializer())
        .create()

    /**
     * База данных приложения.
     */
    private val db = MainApplicationDatabase.database(context)

    /**
     * Dao для работы с расписаниями.
     */
    private val dao = db.schedules()

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
    suspend fun toggleScheduleSyncState(scheduleName: String, sync: Boolean = false) {
        val item = scheduleItem(scheduleName).first()
        if (item != null) {
            item.synced = sync
            if (!sync) item.lastUpdate = null
            updateScheduleItem(item)
        }
    }

    /**
     * Возвращает ScheduleResponse расписания из БД по названию.
     */
    suspend fun scheduleResponse(scheduleName: String): ScheduleResponse {
        val item = scheduleItem(scheduleName).first()
            ?: throw FileNotFoundException("Schedule not exist: $scheduleName")

        return ScheduleResponse(dao.getAllPairs(item.id).first())
    }

    /**
     * Сохраняет расписание на устройство.
     */
    @Throws(RuntimeException::class)
    suspend fun saveToDevice(scheduleName: String, uri: Uri, context: Context) {
        // получаем объект файла по пути
        var documentFile: DocumentFile? = DocumentFile.fromTreeUri(context, uri)

        // регистрируем файл
        documentFile = documentFile?.createFile("application/json", "$scheduleName.json")
        if (documentFile == null) {
            throw RuntimeException("Failed register file on device")
        }

        // uri файла сохранения
        val uriFile = documentFile.uri

        // открывает поток для записи
        val resolver = context.contentResolver

        val stream = resolver.openOutputStream(uriFile)
            ?: throw RuntimeException("Cannot open file stream")

        stream.bufferedWriter().use {
            val response = scheduleResponse(scheduleName)
            val json = gson.toJson(response)
            json.reader().copyTo(it)
        }
    }

    /**
     * Сохраняет ScheduleResponse в БД.
     */
    suspend fun saveResponse(
        scheduleName: String,
        json: String,
        replaceExist: Boolean = false
    ) {
        val response = gson.fromJson(json, ScheduleResponse::class.java)
        saveResponse(scheduleName, response, replaceExist)
    }

    /**
     * Сохраняет ScheduleResponse в БД.
     */
    suspend fun saveResponse(
        scheduleName: String,
        response: ScheduleResponse,
        replaceExist: Boolean = false,
        isSync: Boolean = false
    ) {
        dao.insertScheduleResponse(scheduleName, response, replaceExist, isSync)
    }

    companion object {

        /**
         * Возвращает текущие избранное расписание.
         * Если избранного расписание нет, то null.
         */
        fun favorite(context: Context): String? {
            val scheduleName: String? = SchedulePreference.favorite(context)
            if (scheduleName == null || scheduleName.isEmpty()) {
                return null
            }
            return scheduleName
        }

        /**
         * Устанавливает избранное расписание.
         */
        fun setFavorite(context: Context, scheduleName: String?) {
            SchedulePreference.setFavorite(context, scheduleName)
        }

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