package com.vereshchagin.nikolay.stankinschedule.schedule.repository.domain.repository

import com.vereshchagin.nikolay.stankinschedule.schedule.core.data.api.PairJson
import com.vereshchagin.nikolay.stankinschedule.schedule.repository.data.api.DescriptionResponse
import com.vereshchagin.nikolay.stankinschedule.schedule.repository.data.api.ScheduleItemResponse


interface ScheduleRemoteService {

    suspend fun description(): DescriptionResponse

    suspend fun category(category: String): List<ScheduleItemResponse>

    suspend fun schedule(category: String, schedule: String): List<PairJson>

}