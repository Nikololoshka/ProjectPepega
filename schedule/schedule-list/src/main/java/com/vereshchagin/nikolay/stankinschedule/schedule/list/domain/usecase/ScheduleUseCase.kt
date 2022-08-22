package com.vereshchagin.nikolay.stankinschedule.schedule.list.domain.usecase

import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.ScheduleInfo
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.repository.ScheduleStorage
import com.vereshchagin.nikolay.stankinschedule.schedule.list.data.repository.SchedulePreference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class ScheduleUseCase @Inject constructor(
    private val storage: ScheduleStorage,
    private val preference: SchedulePreference,
) {

    fun schedules(): Flow<List<ScheduleInfo>> = storage.schedules().flowOn(Dispatchers.IO)

    fun favorite(): Flow<Long> = preference.favorite()

    suspend fun setFavorite(id: Long) = preference.setFavorite(id)

    suspend fun updatePositions(list: List<ScheduleInfo>) {
        val newList = list.mapIndexed { index, scheduleInfo -> scheduleInfo.copy(position = index) }
        storage.updateSchedules(newList)
    }

    suspend fun removeSchedules(schedules: List<ScheduleInfo>) {
        storage.removeSchedules(schedules)
    }
}