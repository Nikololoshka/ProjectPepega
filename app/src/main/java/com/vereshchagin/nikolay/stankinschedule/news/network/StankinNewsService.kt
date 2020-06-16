package com.vereshchagin.nikolay.stankinschedule.news.network

import com.vereshchagin.nikolay.stankinschedule.BuildConfig
import com.vereshchagin.nikolay.stankinschedule.news.model.NewsResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Сервис дляи раьоты с API новостей.
 */
class StankinNewsService {

    companion object {
        /**
         * Адрес МГТУ "СТАНКИН"
         */
        const val BASE_URL = "https://stankin.ru"
        val instance = StankinNewsService()
    }

    private val retrofit: Retrofit

    init {
        val builder = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())

        // включение
        if (BuildConfig.DEBUG) {
            val logger = HttpLoggingInterceptor()
            logger.level = HttpLoggingInterceptor.Level.BODY

            val client = OkHttpClient.Builder()
                    .addInterceptor(logger)
                    .build()

            builder.client(client)
        }

        retrofit = builder.build()
    }

    /**
     * Запрос к новосям университета.
     * @param count количество новостей.
     * @param page номер страницы новостей.
     * @param tag тэги поиска новостей.
     * @param query запрос поиска новостей.
     */
    fun getUniversityNews(
            count: Int, page: Int, tag: String = "", query: String = ""
    ) : Call<NewsResponse> {
        val data = mapOf(
                "count" to count,
                "page" to page,
                "is_main" to "false",
                "pull_site" to "false",
                "subdivision_id" to "125",
                "tag" to tag,
                "query_search" to query
        )

        return retrofit.create(StankinNewsApi::class.java)
                .getData(StankinPostData("getNews", data))
    }
}