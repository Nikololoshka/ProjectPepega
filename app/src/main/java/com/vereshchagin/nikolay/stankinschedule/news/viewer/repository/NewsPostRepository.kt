package com.vereshchagin.nikolay.stankinschedule.news.viewer.repository

import com.google.gson.GsonBuilder
import com.vereshchagin.nikolay.stankinschedule.BuildConfig
import com.vereshchagin.nikolay.stankinschedule.news.review.categories.repository.NewsRepository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class NewsPostRepository(private val newsId: Int) {

    private var retrofit: Retrofit
    private var api: StankinNewsPostsApi

    init {
        val builder = Retrofit.Builder()
            .baseUrl(NewsRepository.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(
                GsonBuilder()

                    .create()
            ))

        // включение лога
        if (BuildConfig.DEBUG) {
            val logger = HttpLoggingInterceptor()
            logger.level = HttpLoggingInterceptor.Level.BODY

            val client = OkHttpClient.Builder()
                .addInterceptor(logger)
                .build()

            builder.client(client)
        }

        retrofit = builder.build()
        api = retrofit.create(StankinNewsPostsApi::class.java)
    }
}