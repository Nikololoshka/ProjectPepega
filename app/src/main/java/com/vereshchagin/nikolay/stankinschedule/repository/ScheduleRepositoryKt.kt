package com.vereshchagin.nikolay.stankinschedule.repository

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import com.google.gson.GsonBuilder
import com.vereshchagin.nikolay.stankinschedule.db.MainApplicationDatabase
import com.vereshchagin.nikolay.stankinschedule.model.schedule.ScheduleKt
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

class ScheduleRepositoryKt(
    context: Context,
) {
    val gson = GsonBuilder()
        .registerTypeAdapter(ScheduleResponse::class.java, ScheduleResponse.Serializer())
        .registerTypeAdapter(Pair::class.java, Pair.Serializer())
        .create()

    private val db = MainApplicationDatabase.database(context)
    private val dao = db.schedules()

    fun schedules(): Flow<List<ScheduleItem>> = dao.getAllSchedules()

    fun schedule(scheduleName: String): Flow<ScheduleKt?> = dao.getScheduleWithPairs(scheduleName)
        .map {
            if (it != null) ScheduleKt(it) else null
        }

    fun scheduleItem(scheduleName: String) = dao.getScheduleItem(scheduleName)

    fun schedule(id: Long): Flow<ScheduleKt?> = dao.getScheduleWithPairs(id)
        .map {
            if (it != null) ScheduleKt(it) else null
        }

    suspend fun createSchedule(scheduleName: String) {
        dao.insertScheduleItem(ScheduleItem(scheduleName))
    }

    suspend fun updateScheduleItem(item: ScheduleItem) = dao.updateScheduleItem(item)

    suspend fun removeSchedule(scheduleName: String) = dao.deleteSchedule(scheduleName)

    suspend fun isScheduleExist(scheduleName: String) = dao.isScheduleExist(scheduleName)

    fun pair(id: Long) = dao.getPairItem(id)

    suspend fun updatePair(pair: PairItem) = dao.insertPairItem(pair)

    suspend fun removePair(pair: PairItem) = dao.deletePairItem(pair)

    suspend fun scheduleResponse(scheduleName: String): ScheduleResponse {
        val item = scheduleItem(scheduleName).first()
            ?: throw FileNotFoundException("Schedule not exist: $scheduleName")

        return ScheduleResponse(dao.getAllPairs(item.id))
    }

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

    suspend fun saveResponse(scheduleName: String, json: String) {
        val response = gson.fromJson(json, ScheduleResponse::class.java)
        dao.insertScheduleResponse(scheduleName, response)
    }

    companion object {

        fun favorite(context: Context): String? {
            val scheduleName: String? = SchedulePreference.favorite(context)
            if (scheduleName == null || scheduleName.isEmpty()) {
                return null
            }
            return scheduleName
        }

        fun setFavorite(context: Context, scheduleName: String?) {
            SchedulePreference.setFavorite(context, scheduleName)
        }

        suspend fun saveResponse(
            context: Context,
            scheduleName: String,
            response: ScheduleResponse,
        ) {
            val repository = ScheduleRepositoryKt(context)
            repository.dao.insertScheduleResponse(scheduleName, response)
        }

        suspend fun saveResponse(
            path: String,
            scheduleName: String,
            db: MainApplicationDatabase,
        ) {
            val json = FileUtils.readFileToString(File(path), StandardCharsets.UTF_8)
            val gson = GsonBuilder()
                .registerTypeAdapter(ScheduleResponse::class.java, ScheduleResponse.Serializer())
                .registerTypeAdapter(Pair::class.java, Pair.Serializer())
                .create()

            val response = gson.fromJson(json, ScheduleResponse::class.java)
            db.schedules().insertScheduleResponse(scheduleName, response)
        }
    }
}