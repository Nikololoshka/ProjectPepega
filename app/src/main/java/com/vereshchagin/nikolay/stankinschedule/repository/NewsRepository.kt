package com.vereshchagin.nikolay.stankinschedule.repository

import android.content.Context
import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.paging.LivePagedListBuilder
import com.vereshchagin.nikolay.stankinschedule.BuildConfig
import com.vereshchagin.nikolay.stankinschedule.api.NetworkState
import com.vereshchagin.nikolay.stankinschedule.api.StankinNewsApi
import com.vereshchagin.nikolay.stankinschedule.db.MainApplicationDatabase
import com.vereshchagin.nikolay.stankinschedule.db.dao.NewsDao
import com.vereshchagin.nikolay.stankinschedule.model.news.NewsItem
import com.vereshchagin.nikolay.stankinschedule.model.news.NewsResponse
import com.vereshchagin.nikolay.stankinschedule.repository.boundary.NewsBoundaryCallback
import com.vereshchagin.nikolay.stankinschedule.ui.news.review.categories.paging.Listing
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Репозиторий с новостями.
 */
class NewsRepository(private val newsSubdivision: Int, context: Context) {

    private var retrofit: Retrofit
    private var api: StankinNewsApi

    private var db = MainApplicationDatabase.database(context)
    private var dao: NewsDao

    val ioExecutor: ExecutorService = Executors.newSingleThreadExecutor()

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
        api = retrofit.create(StankinNewsApi::class.java)
        dao = db.news()
    }

    /**
     * Добавляет новости из API запроса в БД.
     * @param body результат запроса к API.
     */
    private fun addPostsIntoDb(body: NewsResponse?) {
        body!!.data.news.let { posts ->
            db.runInTransaction {
                // индекс по порядку
                val start = db.news().nextIndexInResponse(newsSubdivision)
                // добавление индекса и номера отдела
                val items = posts.mapIndexed { index, newsPost ->
                    newsPost.indexInResponse = start + index
                    newsPost.newsSubdivision = newsSubdivision
                    newsPost
                }
                db.news().insert(items)
            }
        }
    }

    /**
     * Обновляет новости в БД.
     */
    @MainThread
    fun refresh() : LiveData<NetworkState> {
        val networkState = MutableLiveData(NetworkState.LOADING)

        StankinNewsApi.getNews(api, newsSubdivision, 1).enqueue(
            object : Callback<NewsResponse> {
                override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
                    networkState.value = NetworkState.error(t.message)
                }

                override fun onResponse(
                    call: Call<NewsResponse>,
                    response: Response<NewsResponse>
                ) {
                    ioExecutor.execute {
                        db.runInTransaction {
                            // удаление старых постов и добавление новых
                            db.news().clear(newsSubdivision)
                            addPostsIntoDb(response.body())
                        }
                        networkState.postValue(NetworkState.LOADED)
                    }
                }
            }
        )
        return networkState
    }

    /**
     * Возвращает объект Listing для отображения.
     * @param size размер пачки загружаемых новостей.
     */
    fun posts(size: Int = 20): Listing<NewsItem> {
        val boundaryCallback =
            NewsBoundaryCallback(
                api, dao, newsSubdivision, ioExecutor, this::addPostsIntoDb
            )

        val refreshTrigger = MutableLiveData<Unit>()
        val refreshState = Transformations.switchMap(refreshTrigger) {
            refresh()
        }

//        val pager = Pager(
//            PagingConfig(40, 20, true),
//            remoteMediator = null
//        ) {
//            db.news().all(newsSubdivision)
//        }

        val pagedList = LivePagedListBuilder(db.news().all(newsSubdivision), size)
            .setBoundaryCallback(boundaryCallback)
            .build()

        return Listing(
            pagedList,
            boundaryCallback.networkState,
            refreshState,
            refresh = {
                refreshTrigger.value = null
            },
            retry = {
                boundaryCallback.helper.retryAllFailed()
            }
        )
    }

    companion object {
        /**
         * Адрес МГТУ "СТАНКИН"
         */
        const val BASE_URL = "https://stankin.ru"
    }
}