package com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.repository

import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.ScheduleModel


interface ScheduleDeviceRepository {
    suspend fun saveToDevice(model: ScheduleModel, path: String)
    suspend fun loadFromDevice(path: String): ScheduleModel
}