package com.vereshchagin.nikolay.stankinschedule.news.viewer.repository

import com.vereshchagin.nikolay.stankinschedule.news.viewer.repository.model.NewsPostResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST


/**
 * API для получения данных с stankin.ru
 */
interface StankinNewsPostsApi {

    /**
     * Запрос к API новостей stankin.ru.
     * @param action дейстивие.
     * @param data данные запроса.
     */
    @POST("/api_entry.php")
    fun getData(@Body data: Map<String, Any>): Call<NewsPostResponse>

    companion object {

        /**
         * Запрос новости с stankin.ru
         * @param api API для получения данных.
         * @param newsId ID новости.
         */
        fun getNewsPost(api: StankinNewsPostsApi, newsId: Int): Call<NewsPostResponse> {
            val data = mapOf(
                "id" to newsId
            )
            return api.getData(mapOf("action" to "getNewsItem", "data" to data))
        }
    }
}