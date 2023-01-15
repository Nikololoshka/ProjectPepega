package com.vereshchagin.nikolay.stankinschedule.schedule.repository.domain.repository

import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.PairModel

interface ScheduleLoaderService {

    suspend fun schedule(category: String, schedule: String): List<PairModel>

}