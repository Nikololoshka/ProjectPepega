package com.vereshchagin.nikolay.stankinschedule.api

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.vereshchagin.nikolay.stankinschedule.model.news.NewsPostResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST


/**
 * API для получения данных с stankin.ru
 */
interface StankinNewsPostsApi {

    /**
     * Запрос к API новостей stankin.ru.
     * @param data данные запроса.
     */
    @POST("/api_entry.php")
    fun getData(@Body data: PostData): Call<NewsPostResponse>

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
            return api.getData(
                PostData(
                    "getNewsItem",
                    data
                )
            )
        }

        /**
         * Объект для POST запроса.
         */
        @Keep
        class PostData(
            @SerializedName("action") val action: String,
            @SerializedName("data") val data: Map<String, Any>
        )
    }
}