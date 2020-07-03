package com.vereshchagin.nikolay.stankinschedule.news.viewer

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vereshchagin.nikolay.stankinschedule.news.viewer.repository.NewsPostRepository
import com.vereshchagin.nikolay.stankinschedule.utils.LoadState
import java.io.File


/**
 * ViewModel для фрагмента с новостью.
 * @param repository репозиторий, откуда будет загружаться новость.
 * @param application объект приложение для доступа к контексту.
 */
class NewsViewerViewModel(
    private val repository: NewsPostRepository,
    val newsId: Int,
    application: Application
) : AndroidViewModel(application) {

    /**
     * Состояние загрузки.
     */
    val state = MutableLiveData(LoadState.LOADING)

    init {
        repository.loadPost(state)
    }

    /**
     * Текущий пост.
     */
    fun post() = repository.newsPost

    /**
     * Обновляет новость.
     */
    fun refresh() {
        if (state.value != LoadState.LOADING) {
            repository.refresh(state)
        }
    }

    /**
     * Factory для создания ViewModel.
     */
    class Factory(
        private val newsId: Int,
        private val application: Application
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return NewsViewerViewModel(
                NewsPostRepository(newsId, cacheDir()), newsId, application
            ) as T
        }

        private fun cacheDir() = File(application.cacheDir, "posts")
    }
}