package com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.repository

import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.PairModel
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.ScheduleInfo
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.ScheduleModel
import kotlinx.coroutines.flow.Flow

interface ScheduleStorage {

    fun schedules(): Flow<List<ScheduleInfo>>

    fun schedule(scheduleId: Long): Flow<ScheduleInfo?>

    fun scheduleModel(scheduleId: Long): Flow<ScheduleModel?>

    fun schedulePair(pairId: Long): Flow<PairModel?>

    suspend fun saveSchedule(model: ScheduleModel, replaceExist: Boolean = false): Long

    suspend fun isScheduleExist(scheduleName: String): Boolean

    suspend fun updateSchedules(schedules: List<ScheduleInfo>)

    suspend fun removeSchedule(id: Long)

    suspend fun removeSchedules(schedules: List<ScheduleInfo>)

    suspend fun removeSchedulePair(pair: PairModel)

    suspend fun renameSchedule(id: Long, scheduleName: String)
}