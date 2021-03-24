package com.vereshchagin.nikolay.stankinschedule.repository

import android.content.Context
import androidx.paging.PagingSource
import androidx.room.withTransaction
import com.vereshchagin.nikolay.stankinschedule.BuildConfig
import com.vereshchagin.nikolay.stankinschedule.api.StankinNewsAPI
import com.vereshchagin.nikolay.stankinschedule.db.MainApplicationDatabase
import com.vereshchagin.nikolay.stankinschedule.db.dao.NewsDao
import com.vereshchagin.nikolay.stankinschedule.model.news.NewsItem
import com.vereshchagin.nikolay.stankinschedule.model.news.NewsResponse
import com.vereshchagin.nikolay.stankinschedule.settings.NewsPreference
import com.vereshchagin.nikolay.stankinschedule.utils.DateUtils
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.await
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

/**
 * Репозиторий с новостями.
 */
class NewsRepository(
    val newsSubdivision: Int, private val context: Context
) {

    private var retrofit: Retrofit
    private var api: StankinNewsAPI

    private val db = MainApplicationDatabase.database(context)
    private var dao: NewsDao

    private var newsValid: Boolean = false

    init {
        val builder = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())

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
        api = retrofit.create(StankinNewsAPI::class.java)
        dao = db.news()

        val date = NewsPreference.lastNewsUpdate(context, newsSubdivision)
        newsValid = date != null && DateUtils.minutesBetween(Calendar.getInstance(), date) < 30
    }

    /**
     * Добавляет новостные посты в БД.
     */
    suspend fun addPostsIntoDb(response: NewsResponse, refresh: Boolean) {
        val items = response.data.news
        if (items.isNotEmpty()) {
            db.withTransaction {
                // если обновляем список
                if (refresh) {
                    dao.clear(newsSubdivision)
                    NewsPreference.setNewsUpdate(context, newsSubdivision, Calendar.getInstance())
                    newsValid = true
                }

                // индекс по порядку
                val start = dao.nextIndexInResponse(newsSubdivision)

                // добавление индекса и номера отдела
                val news = items.mapIndexed { index, newsPost ->
                    newsPost.indexInResponse = start + index
                    newsPost.newsSubdivision = newsSubdivision
                    newsPost
                }
                dao.insert(news)
            }
        }
    }

    /**
     * Возвращает список новостей по номеру страницы и количеству необходимых новостей.
     */
    suspend fun news(page: Int, count: Int = 40): NewsResponse {
        return StankinNewsAPI.getNews(api, newsSubdivision, page, count).await()
    }

    /**
     * Возвращает источник данных новостей (из БД).
     */
    fun pagingSource(): PagingSource<Int, NewsItem> {
        return db.news().all(newsSubdivision)
    }

    /**
     * Обновляет новости в БД, если они больше не действительны.
     */
    suspend fun refresh() {
        if (!newsValid) {
            try {
                val response = news(1)
                addPostsIntoDb(response, true)

            } catch (ignored: Exception) {

            }
        }
    }

    /**
     * Возвращает true, если необходимо обновить новости.
     */
    fun isRequiredRefresh(): Boolean {
        return !newsValid
    }

    companion object {
        /**
         * Адрес МГТУ "СТАНКИН"
         */
        const val BASE_URL = "https://stankin.ru"
    }
}