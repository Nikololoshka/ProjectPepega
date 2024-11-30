package com.vereshchagin.nikolay.stankinschedule.news.core.data.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface StankinNews2024API {

    @GET("/news")
    fun getNewsPage(@Query("PAGEN_1") page: Int): Call<String>

    companion object {
        const val BASE_URL = "https://stankin.ru"
    }
}