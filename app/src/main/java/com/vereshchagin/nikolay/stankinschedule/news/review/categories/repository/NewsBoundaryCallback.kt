package com.vereshchagin.nikolay.stankinschedule.news.review.categories.repository

import android.util.Log
import androidx.paging.PagedList
import com.vereshchagin.nikolay.stankinschedule.news.review.categories.repository.db.NewsDao
import com.vereshchagin.nikolay.stankinschedule.news.review.categories.repository.model.NewsItem
import com.vereshchagin.nikolay.stankinschedule.news.review.categories.repository.model.NewsResponse
import com.vereshchagin.nikolay.stankinschedule.news.review.categories.repository.network.StankinNewsApi
import com.vereshchagin.nikolay.stankinschedule.utils.PagingRequestHelper
import com.vereshchagin.nikolay.stankinschedule.utils.createStatusLiveData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.Executor
import kotlin.math.roundToLong

/**
 * Callback для дозагрузки данных из сети.
 * @param api объект API для осуществления запросов.
 * @param dao интерфейс для работы с БД.
 * @param newsSubdivision номер отдела новостей.
 * @param ioExecutor executor для операций чтения/записи.
 * @param handelResponse функция для обработки ответа от сервера.
 */
class NewsBoundaryCallback(
    private val api: StankinNewsApi,
    private val dao: NewsDao,
    private val newsSubdivision: Int,
    private val ioExecutor: Executor,
    private val handelResponse: (NewsResponse?) -> Unit
) : PagedList.BoundaryCallback<NewsItem>() {

    val helper = PagingRequestHelper(ioExecutor)
    val networkState = helper.createStatusLiveData()

    override fun onZeroItemsLoaded() {
        helper.runIfNotRunning(PagingRequestHelper.RequestType.INITIAL) {
            StankinNewsApi.getNews(api, newsSubdivision, 1)
                .enqueue(apiCallback(it))
        }
    }

    override fun onItemAtEndLoaded(itemAtEnd: NewsItem) {
        ioExecutor.execute {
            val count = dao.count(newsSubdivision)
            Log.d("MyLog", count.toString())

            helper.runIfNotRunning(PagingRequestHelper.RequestType.AFTER) {
                StankinNewsApi.getNews(api, newsSubdivision, (count / 40.0).roundToLong() + 1)
                    .enqueue(apiCallback(it))
            }
        }
    }

    /**
     * Добавляет новости в БД.
     * @param response ответ от сервера.
     * @param callback helper callback для paging'а новостей.
     */
    private fun addPostsIntoDb(response: Response<NewsResponse>,
                               callback: PagingRequestHelper.Request.Callback) {
        ioExecutor.execute {
            handelResponse(response.body())
            callback.recordSuccess()
        }
    }

    /**
     * Callback для StankinNewsAPI.
     * @param callback helper callback для paging'а новостей.
     */
    private fun apiCallback(callback: PagingRequestHelper.Request.Callback): Callback<NewsResponse> {
        return object : Callback<NewsResponse> {
            override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
                callback.recordFailure(t)
            }

            override fun onResponse(call: Call<NewsResponse>, response: Response<NewsResponse>) {
                addPostsIntoDb(response, callback)
            }
        }
    }
}