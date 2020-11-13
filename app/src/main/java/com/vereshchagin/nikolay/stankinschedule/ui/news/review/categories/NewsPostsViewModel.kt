package com.vereshchagin.nikolay.stankinschedule.ui.news.review.categories

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.vereshchagin.nikolay.stankinschedule.repository.NewsRepository
import com.vereshchagin.nikolay.stankinschedule.ui.news.review.categories.paging.NewsPostRemoteMediator

/**
 * ViewModel для фрагмента с списком новостей.
 * @param repository репозиторий, откуда будут загружаться новости.
 * @param application объект приложение для доступа к контексту.
 */
@ExperimentalPagingApi
class NewsPostsViewModel(
    private val repository: NewsRepository,
    application: Application
) : AndroidViewModel(application) {

    /**
     * Новостные посты отдела.
     */
    val posts = Pager(
        PagingConfig(
            NEWS_PAGE_SIZE,
            prefetchDistance = NEWS_PAGE_SIZE / 2,
            enablePlaceholders = false,
            initialLoadSize = NEWS_PAGE_SIZE
        ),
        remoteMediator = NewsPostRemoteMediator(repository)
    ) {
        repository.pagingSource()
    }.liveData.cachedIn(viewModelScope)

    /**
     * Factory для создания ViewModel.
     */
    class Factory(
        private val newsSubdivision: Int, private val application: Application
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return NewsPostsViewModel(
                NewsRepository(
                    newsSubdivision,
                    application.applicationContext
                ),
                application
            ) as T
        }
    }

    companion object {
        private const val NEWS_PAGE_SIZE = 40
    }
}