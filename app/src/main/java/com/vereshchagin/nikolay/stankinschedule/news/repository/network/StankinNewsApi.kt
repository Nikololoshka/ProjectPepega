package com.vereshchagin.nikolay.stankinschedule.news.repository.network

import com.vereshchagin.nikolay.stankinschedule.news.repository.model.NewsResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

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


    companion object {
        /**
         * Запрос к API новостей stankin.ru.
         * @param subdivision номер отдела, чьи новости нужны.
         * @param page номер страницы.
         * @param count количество новостей.
         * @param tag тэги для новостей.
         * @param query фильтр для новостей.
         */
        fun getNews(
            api: StankinNewsApi, subdivision: Int, page: Int,
            count: Int = 40, tag: String = "", query: String = ""
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
            return api.getData(StankinPostData("getNews", data))
        }
    }
}