package com.vereshchagin.nikolay.stankinschedule.news.network

import androidx.paging.PagedList
import com.vereshchagin.nikolay.stankinschedule.news.model.NewsPost
import com.vereshchagin.nikolay.stankinschedule.news.model.NewsResponse
import com.vereshchagin.nikolay.stankinschedule.utils.PagingRequestHelper
import com.vereshchagin.nikolay.stankinschedule.utils.createStatusLiveData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.Executor

/**
 * Callback для дозагрузки данных из сети.
 */
class NewsBoundaryCallback(
    private val api: StankinNewsApi,
    private val ioExecutor: Executor,
    private val handelResponse: (NewsResponse?) -> Unit
) : PagedList.BoundaryCallback<NewsPost>() {

    val helper = PagingRequestHelper(ioExecutor)
    val networkState = helper.createStatusLiveData()

    override fun onZeroItemsLoaded() {
        println("onZeroItemsLoaded")
        helper.runIfNotRunning(PagingRequestHelper.RequestType.INITIAL) {
            StankinNewsApi.getUniversityNews(api,1).enqueue(apiCallback(it))
        }
    }

    override fun onItemAtEndLoaded(itemAtEnd: NewsPost) {
        helper.runIfNotRunning(PagingRequestHelper.RequestType.AFTER) {
            StankinNewsApi.getUniversityNews(api,2).enqueue(apiCallback(it))
        }
    }

    private fun addPostsIntoDb(response: Response<NewsResponse>,
                               it: PagingRequestHelper.Request.Callback) {
        ioExecutor.execute {
            handelResponse(response.body())
            it.recordSuccess()
        }
    }

    /**
     * Callback для StankinNewsAPI.
     */
    private fun apiCallback(it: PagingRequestHelper.Request.Callback)
        : Callback<NewsResponse> {
        return object : Callback<NewsResponse> {
            override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
                it.recordFailure(t)
            }

            override fun onResponse(call: Call<NewsResponse>, response: Response<NewsResponse>) {
                addPostsIntoDb(response, it)
            }
        }
    }
}