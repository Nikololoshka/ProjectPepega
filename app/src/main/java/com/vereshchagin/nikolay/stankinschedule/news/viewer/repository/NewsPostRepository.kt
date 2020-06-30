package com.vereshchagin.nikolay.stankinschedule.news.viewer.repository

import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.vereshchagin.nikolay.stankinschedule.BuildConfig
import com.vereshchagin.nikolay.stankinschedule.news.viewer.repository.api.StankinNewsPostsApi
import com.vereshchagin.nikolay.stankinschedule.news.viewer.repository.model.NewsPost
import com.vereshchagin.nikolay.stankinschedule.news.viewer.repository.model.NewsPostResponse
import com.vereshchagin.nikolay.stankinschedule.utils.LoadState
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.apache.commons.io.FileUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Репозиторий для получения постов новостей.
 */
class NewsPostRepository(private val newsId: Int, private val cacheDir: File) {

    private var retrofit: Retrofit
    private var api: StankinNewsPostsApi

    private val ioExecutor: ExecutorService = Executors.newSingleThreadExecutor()
    var newsPost: NewsPost? = null

    init {
        val builder = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(
                GsonBuilder()
                    .registerTypeAdapter(NewsPost::class.java, NewsPost.NewsPostDeserializer())
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

    /**
     * Загружает пост либо из кэша, либо из интернета.
     * @param state LiveData для уведомления статуса загрузки.
     */
    fun loadPost(state: MutableLiveData<LoadState>) {
        state.value = LoadState.LOADING
        ioExecutor.execute {
            val post = loadFromCache()
            if (post != null) {
                newsPost = post
                state.postValue(LoadState.LOADED)
            } else {
                loadFromNetwork(state)
            }
        }
    }

    /**
     * Обновляет пост.
     * @param state LiveData для уведомления статуса загрузки.
     */
    fun refresh(state: MutableLiveData<LoadState>) {
        state.value = LoadState.LOADING
        ioExecutor.execute {
            loadFromNetwork(state)
        }
    }

    /**
     * Загружает пост из интернета.
     * @param state LiveData для уведомления статуса загрузки.
     */
    private fun loadFromNetwork(state: MutableLiveData<LoadState>) {
        StankinNewsPostsApi.getNewsPost(api, newsId)
            .enqueue(object : Callback<NewsPostResponse> {
                override fun onFailure(call: Call<NewsPostResponse>, t: Throwable) {
                    state.value = LoadState.error(t.message ?: "")
                }
                override fun onResponse(call: Call<NewsPostResponse>, response: Response<NewsPostResponse>) {
                    ioExecutor.execute {
                        val post = response.body()?.data
                        if (post != null) {
                            saveToCache(post)
                            newsPost = post
                            state.postValue(LoadState.LOADED)

                        } else {
                            state.postValue(LoadState.error(response.errorBody()?.string() ?: ""))
                        }
                    }
                }
            })
    }

    /**
     * Загружает пост из кэша. Если не удалось, то null.
     */
    private fun loadFromCache(): NewsPost? {
        try {
            val file = File(cacheDir, "$newsId.json")
            if (!file.exists()) {
                return null
            }
            val json = FileUtils.readFileToString(file, Charsets.UTF_8)
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
            val file = File(cacheDir, "$newsId.json")
            val json = Gson().toJson(post)
            FileUtils.writeStringToFile(file, json, Charsets.UTF_8)
        } catch (ignored: Exception) {

        }
    }

    companion object {
        /**
         * Адрес МГТУ "СТАНКИН"
         */
        const val BASE_URL = "https://stankin.ru"
    }
}