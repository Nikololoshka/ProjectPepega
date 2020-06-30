package com.vereshchagin.nikolay.stankinschedule.news.viewer

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vereshchagin.nikolay.stankinschedule.news.viewer.repository.NewsPostRepository
import com.vereshchagin.nikolay.stankinschedule.utils.LoadState
import java.io.File

class NewsViewerViewModel(
    private val repository: NewsPostRepository, application: Application
) : AndroidViewModel(application) {

    val state = MutableLiveData(LoadState.LOADING)

    init {
        repository.loadPost(state)
    }

    fun post() = repository.newsPost

    class Factory(
        private val newsId: Int,
        private val application: Application
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return NewsViewerViewModel(
                NewsPostRepository(newsId, cacheDir()), application
            ) as T
        }

        private fun cacheDir() = File(application.cacheDir, "posts")
    }
}