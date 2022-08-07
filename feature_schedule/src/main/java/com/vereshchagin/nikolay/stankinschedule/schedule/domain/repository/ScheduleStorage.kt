package com.vereshchagin.nikolay.stankinschedule.schedule.domain.repository

import com.vereshchagin.nikolay.stankinschedule.schedule.domain.model.ScheduleModel

interface ScheduleStorage {

    suspend fun saveSchedule(model: ScheduleModel, replaceExist: Boolean = false)

    suspend fun isScheduleExist(scheduleName: String): Boolean

}