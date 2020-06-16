package com.vereshchagin.nikolay.stankinschedule.news.network

import com.vereshchagin.nikolay.stankinschedule.news.model.NewsResponse
import retrofit2.Call
import retrofit2.http.*

/**
 * API для получения данных с stankin.ru
 */
interface StankinNewsApi {
    /**
     * Запрос к API новостей stankin.ru.
     * @param action дейстивие.
     * @param data данные запроса.
     */
    @POST("/api_entry.php")
    fun getData(@Body data: StankinPostData): Call<NewsResponse>
}