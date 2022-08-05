package com.vereshchagin.nikolay.stankinschedule.schedule.domain.repository

import com.vereshchagin.nikolay.stankinschedule.schedule.data.api.DescriptionResponse
import com.vereshchagin.nikolay.stankinschedule.schedule.data.api.PairResponse
import com.vereshchagin.nikolay.stankinschedule.schedule.data.api.ScheduleItemResponse

interface ScheduleRemoteService {

    suspend fun description(): DescriptionResponse

    suspend fun category(category: String): List<ScheduleItemResponse>

    suspend fun schedule(category: String, schedule: String): List<PairResponse>

}