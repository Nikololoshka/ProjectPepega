package com.vereshchagin.nikolay.stankinschedule.news.core.data.api

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * API для получения данных с stankin.ru
 */
interface StankinNewsAPI {
    /**
     * Запрос к API новостей stankin.ru.
     * @param data данные запроса.
     */
    @POST("/api_entry.php")
    fun getNewsResponse(@Body data: PostData): Call<NewsResponse>

    /**
     * Запрос к API новостей stankin.ru.
     * @param data данные запроса.
     */
    @POST("/api_entry.php")
    fun getPostResponse(@Body data: PostData): Call<PostResponse>

    companion object {

        const val BASE_URL = "https://stankin.ru"

        /**
         * Запрос к API новостей stankin.ru.
         * @param api API для получения данных.
         * @param subdivision номер отдела, чьи новости нужны.
         * @param page номер страницы.
         * @param count количество новостей.
         * @param tag тэги для новостей.
         * @param query фильтр для новостей.
         */
        fun getNews(
            api: StankinNewsAPI,
            subdivision: Int,
            page: Int,
            count: Int = 40,
            tag: String = "",
            query: String = "",
        ): Call<NewsResponse> {
            val data = mapOf(
                "count" to count,
                "page" to page,
                "is_main" to false,
                "pull_site" to false,
                "subdivision_id" to subdivision,
                "tag" to tag,
                "query_search" to query
            )
            return api.getNewsResponse(
                PostData(
                    "getNews",
                    data
                )
            )
        }

        /**
         * Запрос новости с stankin.ru
         * @param api API для получения данных.
         * @param newsId ID новости.
         */
        fun getNewsPost(
            api: StankinNewsAPI,
            newsId: Int,
        ): Call<PostResponse> {
            val data = mapOf(
                "id" to newsId
            )
            return api.getPostResponse(
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
            @SerializedName("data") val data: Map<String, Any>,
        )
    }
}