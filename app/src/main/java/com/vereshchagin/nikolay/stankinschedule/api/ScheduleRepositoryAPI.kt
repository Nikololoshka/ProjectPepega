package com.vereshchagin.nikolay.stankinschedule.api

import com.vereshchagin.nikolay.stankinschedule.model.schedule.ScheduleResponse
import com.vereshchagin.nikolay.stankinschedule.model.schedule.repository.v1.RepositoryResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url

/**
 * API для работы с репозиторием расписаний.
 */
interface ScheduleRepositoryAPI {
    /**
     * Получение расписание по ссылке.
     */
    @Streaming
    @GET
    fun schedule(@Url url: String): Call<ScheduleResponse>

    /**
     * Получения информации о репозитории по ссылке.
     */
    @Streaming
    @GET
    fun entry(@Url url: String): Call<RepositoryResponse>
}