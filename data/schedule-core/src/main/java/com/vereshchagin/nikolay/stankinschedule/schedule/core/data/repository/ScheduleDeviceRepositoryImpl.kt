package com.vereshchagin.nikolay.stankinschedule.schedule.core.data.repository

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.vereshchagin.nikolay.stankinschedule.schedule.core.data.api.PairJson
import com.vereshchagin.nikolay.stankinschedule.schedule.core.data.mapper.toJson
import com.vereshchagin.nikolay.stankinschedule.schedule.core.data.mapper.toPairModel
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.ScheduleInfo
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.ScheduleModel
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.repository.ScheduleDeviceRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.FileNotFoundException
import javax.inject.Inject

class ScheduleDeviceRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : ScheduleDeviceRepository {

    override suspend fun saveToDevice(model: ScheduleModel, path: String) {
        val json = model.toJson()

        val contentResolver = context.contentResolver
        contentResolver.openOutputStream(Uri.parse(path)).use { stream ->
            if (stream == null) throw IllegalAccessException("Failed to get file descriptor")

            stream.bufferedWriter().use { writer ->
                Gson().toJson(json, writer)
            }
        }
    }

    override suspend fun loadFromDevice(path: String): ScheduleModel {
        val contentResolver = context.contentResolver

        val uri = Uri.parse(path)
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

        return model
    }

    private fun Uri.extractFileName(contentResolver: ContentResolver): String? {
        return contentResolver.query(this, null, null, null, null)?.use { cursor ->
            cursor.moveToFirst()
            val columnIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (columnIndex >= 0) cursor.getString(columnIndex) else null
        }
    }
}