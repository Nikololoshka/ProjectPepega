package com.vereshchagin.nikolay.stankinschedule.schedule.repository.data.api

import com.vereshchagin.nikolay.stankinschedule.schedule.core.data.api.PairJson
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url

interface ScheduleRepositoryAPI {

    /**
     * Получение описания репозитория по ссылке.
     */
    @Streaming
    @GET
    fun description(@Url url: String): Call<DescriptionResponse>

    /**
     * Получение расписание по ссылке.
     */
    @Streaming
    @GET
    fun schedule(@Url url: String): Call<List<PairJson>>

    companion object {
        const val FIREBASE_URL =
            "https://firebasestorage.googleapis.com/v0/b/stankinschedule.appspot.com/o/"
    }
}