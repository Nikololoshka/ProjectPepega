package com.vereshchagin.nikolay.stankinschedule.news.post

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vereshchagin.nikolay.stankinschedule.news.network.NewsPostRepository
import com.vereshchagin.nikolay.stankinschedule.news.network.StankinNewsRepository


class NewsPostViewModel(private val repository: NewsPostRepository) : ViewModel() {

    private val testVar = MutableLiveData<Int>()
    private val repoResult = Transformations.map(testVar) {
        // it???????????
        repository.posts(it, 20)
    }
    val posts = Transformations.switchMap(repoResult) { it.pagedList }!!
    val networkState = Transformations.switchMap(repoResult) { it.networkState }!!
    val refreshState = Transformations.switchMap(repoResult) { it.refreshState }!!

    init {
        testVar.value = 1
    }

    fun refresh() {
        repoResult.value?.refresh?.invoke()
    }

    fun retry() {
        val listing = repoResult?.value
        listing?.retry?.invoke()
    }

    class Factory(private val context: Context) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return NewsPostViewModel(StankinNewsRepository(context)) as T
        }
    }
}