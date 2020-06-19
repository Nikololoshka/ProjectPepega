package com.vereshchagin.nikolay.stankinschedule.news.network

import android.content.Context
import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.paging.LivePagedListBuilder
import androidx.room.Room
import com.vereshchagin.nikolay.stankinschedule.BuildConfig
import com.vereshchagin.nikolay.stankinschedule.news.db.NewsDao
import com.vereshchagin.nikolay.stankinschedule.news.db.NewsDb
import com.vereshchagin.nikolay.stankinschedule.news.model.NewsPost
import com.vereshchagin.nikolay.stankinschedule.news.model.NewsResponse
import com.vereshchagin.nikolay.stankinschedule.news.post.paging.Listing
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.Executors


class StankinNewsRepository(context: Context) : NewsPostRepository {

    private var retrofit: Retrofit
    private var api: StankinNewsApi

    private var db = Room.databaseBuilder(context, NewsDb::class.java, "database").build()
    private var dao: NewsDao

    val ioExecutor = Executors.newSingleThreadExecutor()

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

    private fun addPostsIntoDb(body: NewsResponse?) {
        body!!.data.news.let { posts ->
            db.runInTransaction {
                db.news().insert(posts)
            }
        }
    }

    @MainThread
    private fun refresh() : LiveData<NetworkState> {
        val networkState = MutableLiveData(NetworkState.LOADING)
        StankinNewsApi.getUniversityNews(api,1).enqueue(
            object : Callback<NewsResponse> {
                override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
                    networkState.value = NetworkState.error(t.message)
                }

                override fun onResponse(call: Call<NewsResponse>, response: Response<NewsResponse>) {
                    ioExecutor.execute {
                        db.runInTransaction {
                            // удаление старых постов
                            db.news().clear()
                            addPostsIntoDb(response.body())
                        }
                        networkState.postValue(NetworkState.LOADED)
                    }
                }
            }
        )
        return networkState
    }

    override fun posts(page: Int, size: Int): Listing<NewsPost> {
        println("posts method")

        val boundaryCallback = NewsBoundaryCallback(
            api, ioExecutor, this::addPostsIntoDb
        )

        val refreshTrigger = MutableLiveData<Unit>()
        val refreshState = Transformations.switchMap(refreshTrigger) {
            refresh()
        }

        val pagedList = LivePagedListBuilder(db.news().all(), size)
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