package com.vereshchagin.nikolay.stankinschedule.schedule.viewer.domain.usecase

import android.content.Context
import android.net.Uri
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.google.gson.Gson
import com.vereshchagin.nikolay.stankinschedule.schedule.core.data.mapper.toJson
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.ScheduleInfo
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.ScheduleModel
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.repository.ScheduleStorage
import com.vereshchagin.nikolay.stankinschedule.schedule.core.ui.data.ScheduleViewDay
import com.vereshchagin.nikolay.stankinschedule.schedule.viewer.data.source.ScheduleViewerSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import org.joda.time.LocalDate
import javax.inject.Inject

class ScheduleViewerUseCase @Inject constructor(
    private val storage: ScheduleStorage,
) {
    fun createPager(
        schedule: ScheduleModel,
        initialDay: LocalDate = LocalDate.now(),
    ): Pager<LocalDate, ScheduleViewDay> {
        return Pager(
            config = PagingConfig(
                pageSize = 30,
                initialLoadSize = 30,
                prefetchDistance = 10,
                enablePlaceholders = false
            ),
            initialKey = initialDay,
            pagingSourceFactory = {
                ScheduleViewerSource(schedule = schedule)
            }
        )
    }

    fun scheduleInfo(scheduleId: Long): Flow<ScheduleInfo?> = storage.schedule(scheduleId)

    fun scheduleModel(scheduleId: Long): Flow<ScheduleModel?> = storage.scheduleModel(scheduleId)

    suspend fun removeSchedule(scheduleId: Long) = withContext(Dispatchers.IO) {
        storage.removeSchedule(scheduleId)
    }

    fun renameSchedule(scheduleId: Long, scheduleName: String): Flow<Boolean> = flow {
        if (storage.isScheduleExist(scheduleName)) {
            emit(false)
            return@flow
        }

        storage.renameSchedule(scheduleId, scheduleName)
        emit(true)

    }.flowOn(Dispatchers.IO)

    fun saveToDevice(context: Context, scheduleId: Long, uri: Uri): Flow<Boolean> = flow {

        val schedule = storage.scheduleModel(scheduleId).firstOrNull()
            ?: throw RuntimeException("Schedule not found")

        val json = schedule.toJson()

        val contentResolver = context.contentResolver
        contentResolver.openOutputStream(uri).use { stream ->
            if (stream == null) throw IllegalAccessException("Failed to get file descriptor")

            stream.bufferedWriter().use { writer ->
                Gson().toJson(json, writer)
            }
        }

        emit(true)
    }.flowOn(Dispatchers.IO)
}