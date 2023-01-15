package com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.usecase

import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.ScheduleInfo
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.ScheduleModel
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.repository.ScheduleStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ScheduleUseCase @Inject constructor(
    private val storage: ScheduleStorage,
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

    fun schedules(): Flow<List<ScheduleInfo>> =
        storage.schedules()
            .flowOn(Dispatchers.IO)

    fun scheduleInfo(scheduleId: Long): Flow<ScheduleInfo?> =
        storage.schedule(scheduleId)
            .flowOn(Dispatchers.IO)

    fun scheduleModel(scheduleId: Long): Flow<ScheduleModel?> =
        storage.scheduleModel(scheduleId)
            .flowOn(Dispatchers.IO)

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

    suspend fun updatePositions(list: List<ScheduleInfo>) {
        val newList = list.mapIndexed { index, scheduleInfo -> scheduleInfo.copy(position = index) }
        storage.updateSchedules(newList)
    }

    suspend fun removeSchedules(schedules: List<ScheduleInfo>) {
        storage.removeSchedules(schedules)
    }
}