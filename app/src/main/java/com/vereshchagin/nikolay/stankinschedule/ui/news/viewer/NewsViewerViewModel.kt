package com.vereshchagin.nikolay.stankinschedule.ui.news.viewer

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.vereshchagin.nikolay.stankinschedule.model.news.NewsPost
import com.vereshchagin.nikolay.stankinschedule.repository.NewsPostRepository
import com.vereshchagin.nikolay.stankinschedule.utils.State
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


/**
 * ViewModel для фрагмента с новостью.
 * @param repository репозиторий, откуда будет загружаться новость.
 * @param application объект приложение для доступа к контексту.
 */
class NewsViewerViewModel(
    private val repository: NewsPostRepository,
    val newsId: Int
) : ViewModel() {

    /**
     * Пост с новостью.
     */
    val post = MutableLiveData<State<NewsPost>>(null)

    init {
        refresh()
    }

    /**
     * Обновляет новость.
     */
    fun refresh(useCache: Boolean = true) {
        if (post.value !is State.Loading) {
            viewModelScope.launch {
                repository.loadPost(useCache)
                    .catch {
                        FirebaseCrashlytics.getInstance()
                            .recordException(it)
                    }
                    .collect {
                        post.postValue(it)
                    }
            }
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
                NewsPostRepository(
                    newsId,
                    application.cacheDir
                ), newsId
            ) as T
        }
    }
}