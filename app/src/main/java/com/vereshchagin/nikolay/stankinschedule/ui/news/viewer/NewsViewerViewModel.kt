package com.vereshchagin.nikolay.stankinschedule.ui.news.viewer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.vereshchagin.nikolay.stankinschedule.model.news.NewsPost
import com.vereshchagin.nikolay.stankinschedule.repository.NewsPostRepository
import com.vereshchagin.nikolay.stankinschedule.utils.State
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch


/**
 * ViewModel для фрагмента с новостью.
 * @param repository репозиторий, откуда будет загружаться новость.
 * @param newsId номер новости.
 */
// @HiltViewModel
class NewsViewerViewModel @AssistedInject constructor(
    private val repository: NewsPostRepository,
    @Assisted private val newsId: Int,
) : ViewModel() {


    private val _post = MutableStateFlow<State<NewsPost>?>(null)

    /**
     * Пост с новостью.
     */
    val post = _post.asStateFlow()


    init {
        refresh()
    }

    /**
     * Обновляет новость.
     */
    fun refresh(useCache: Boolean = true) {
        if (_post.value !is State.Loading) {
            viewModelScope.launch {
                repository.loadPost(newsId, useCache)
                    .catch {
                        FirebaseCrashlytics.getInstance()
                            .recordException(it)
                    }
                    .collect {
                        _post.value = it
                    }
            }
        }
    }

    /**
     * Factory для создания ViewModel.
     */
    @AssistedFactory
    interface NewsViewerFactory {
        fun create(newsId: Int): NewsViewerViewModel
    }

    companion object {
        /**
         * Создает объект в Factory c переданными параметрами.
         */
        fun provideFactory(
            factory: NewsViewerFactory,
            newsId: Int,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return factory.create(newsId) as T
            }
        }
    }
}