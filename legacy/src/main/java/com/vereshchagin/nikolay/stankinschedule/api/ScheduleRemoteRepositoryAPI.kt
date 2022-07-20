package com.vereshchagin.nikolay.stankinschedule.api

import com.vereshchagin.nikolay.stankinschedule.model.schedule.json.JsonScheduleItem
import com.vereshchagin.nikolay.stankinschedule.model.schedule.remote.ScheduleCategoryEntry
import com.vereshchagin.nikolay.stankinschedule.model.schedule.remote.ScheduleItemEntry
import com.vereshchagin.nikolay.stankinschedule.model.schedule.remote.ScheduleRepositoryInfo
import com.vereshchagin.nikolay.stankinschedule.model.schedule.remote.ScheduleUpdateEntry
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


/**
 * API для работы с репозиторием расписаний.
 */
interface ScheduleRemoteRepositoryAPI {

    @GET("$API_ROOT/info?categories=true")
    fun info(): Call<ScheduleRepositoryInfo>

    @GET("$API_ROOT/categories")
    fun categories(@Query("parent") parentCategory: Int): Call<List<ScheduleCategoryEntry>>

    @GET("$API_ROOT/schedules")
    fun schedules(@Query("category") category: Int): Call<List<ScheduleItemEntry>>

    @GET("$API_ROOT/updates/{id}")
    fun updates(@Path("id") scheduleId: Int): Call<List<ScheduleUpdateEntry>>


    @GET("${API_ROOT}/schedules/{id}")
    fun schedulePairs(
        @Path("id") scheduleId: Int,
        @Query("update") scheduleUpdateId: Int,
    ): Call<JsonScheduleItem>


    companion object {
        /**
         * Путь до API удаленного репозитория.
         */
        private const val API_ROOT = "stankinschedule/api/v0"
    }
}