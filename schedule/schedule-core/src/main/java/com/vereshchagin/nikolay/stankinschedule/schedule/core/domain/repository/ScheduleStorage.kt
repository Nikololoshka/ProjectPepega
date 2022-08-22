package com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.repository

import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.ScheduleInfo
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.ScheduleModel
import kotlinx.coroutines.flow.Flow

interface ScheduleStorage {

    fun schedules(): Flow<List<ScheduleInfo>>

    fun schedule(scheduleId: Long): Flow<ScheduleInfo?>

    fun scheduleModel(scheduleId: Long): Flow<ScheduleModel?>

    suspend fun saveSchedule(model: ScheduleModel, replaceExist: Boolean = false)

    suspend fun isScheduleExist(scheduleName: String): Boolean

    suspend fun updateSchedules(schedules: List<ScheduleInfo>)

    suspend fun removeSchedules(schedules: List<ScheduleInfo>)

}