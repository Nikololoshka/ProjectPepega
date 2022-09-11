package com.vereshchagin.nikolay.stankinschedule.schedule.creator.domain.usecase

import android.content.Context
import android.net.Uri
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.vereshchagin.nikolay.stankinschedule.schedule.core.data.api.PairJson
import com.vereshchagin.nikolay.stankinschedule.schedule.core.data.mapper.toPairModel
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.ScheduleInfo
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.ScheduleModel
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.repository.ScheduleStorage
import com.vereshchagin.nikolay.stankinschedule.schedule.creator.utils.extractFileName
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.FileNotFoundException
import javax.inject.Inject

class ScheduleCreatorUseCase @Inject constructor(
    private val storage: ScheduleStorage
) {
    fun createEmptySchedule(scheduleName: String): Flow<Boolean> = flow {
        if (storage.isScheduleExist(scheduleName)) {
            emit(false)
            return@flow
        }

        val info = ScheduleInfo(scheduleName)
        val model = ScheduleModel(info)
        storage.saveSchedule(model)

        emit(true)
    }

    fun importSchedule(context: Context, uri: Uri): Flow<String> = flow {
        val contentResolver = context.contentResolver

        val scheduleName = uri.extractFileName(contentResolver)?.substringBeforeLast('.')
            ?: throw FileNotFoundException("Failed to get file descriptor")

        val json: List<PairJson> = contentResolver.openInputStream(uri).use { stream ->
            if (stream == null) throw IllegalAccessException("Failed to get file descriptor")

            stream.bufferedReader().use { reader ->
                Gson().fromJson(reader, object : TypeToken<List<PairJson>>() {}.type)
            }
        }

        val pairs = json.map { it.toPairModel() }

        val info = ScheduleInfo(scheduleName)
        val model = ScheduleModel(info)
        pairs.forEach { model.add(it) }

        storage.saveSchedule(model)

        emit(scheduleName)
    }
}