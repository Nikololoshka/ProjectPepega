package com.vereshchagin.nikolay.stankinschedule.repository

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.vereshchagin.nikolay.stankinschedule.BuildConfig
import com.vereshchagin.nikolay.stankinschedule.api.StankinNewsPostsApi
import com.vereshchagin.nikolay.stankinschedule.model.news.NewsPost
import com.vereshchagin.nikolay.stankinschedule.utils.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.apache.commons.io.FileUtils
import retrofit2.Retrofit
import retrofit2.await
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

/**
 * Репозиторий для получения постов новостей.
 */
class NewsPostRepository(private val newsId: Int, private val cacheDir: File) {

    private var retrofit: Retrofit
    private var api: StankinNewsPostsApi

    init {
        val builder = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder()
                        .registerTypeAdapter(NewsPost::class.java, NewsPost.NewsPostDeserializer())
                        .create()
                )
            )

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

    /**
     * Загружает пост либо из кэша, либо из интернета.
     */
    suspend fun loadPost(useCache: Boolean = true) = flow<State<NewsPost>> {
        emit(State.loading())

        try {
            // загрузка из кэша
            if (useCache) {
                val cache = loadFromCache()
                if (cache != null) {
                    emit(State.success(cache))
                    return@flow
                }
            }
            // загрузка из сети
            val post = loadFromNetwork()
            emit(State.success(post))

        } catch (e: Exception) {
            emit(State.failed(e))
        }
    }.flowOn(Dispatchers.IO)

    /**
     * Загружает пост из интернета.
     */
    private suspend fun loadFromNetwork(): NewsPost {
        val post = StankinNewsPostsApi.getNewsPost(api, newsId).await().data
        saveToCache(post)
        return post
    }

    /**
     * Загружает пост из кэша. Если не удалось, то null.
     */
    private fun loadFromCache(): NewsPost? {
        try {
            val json = FileUtils.readFileToString(
                FileUtils.getFile(cacheDir, POSTS_FOLDER, "$newsId.json"),
                Charsets.UTF_8
            )
            return Gson().fromJson(json, NewsPost::class.java)

        } catch (ignored: Exception) {

        }

        return null
    }

    /**
     * Сохраняет пост в кэш.
     */
    private fun saveToCache(post: NewsPost) {
        try {
            val json = Gson().toJson(post)
            FileUtils.writeStringToFile(
                FileUtils.getFile(cacheDir, POSTS_FOLDER, "$newsId.json"),
                json,
                Charsets.UTF_8
            )

        } catch (ignored: Exception) {

        }
    }

    companion object {
        /**
         * Адрес МГТУ "СТАНКИН"
         */
        const val BASE_URL = "https://stankin.ru"

        /**
         * Папка кэша.
         */
        const val POSTS_FOLDER = "posts"
    }
}