package com.vereshchagin.nikolay.stankinschedule.api

import com.vereshchagin.nikolay.stankinschedule.model.schedule.ScheduleResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url

/**
 * API для работы с репозиторием расписаний.
 */
interface ScheduleRepositoryApi {
    /**
     * Получение расписание по ссылке.
     */
    @Streaming
    @GET
    fun schedule(@Url url: String): Call<ScheduleResponse>
}