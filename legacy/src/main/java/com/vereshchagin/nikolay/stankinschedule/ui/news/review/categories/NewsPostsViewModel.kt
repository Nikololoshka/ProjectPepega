package com.vereshchagin.nikolay.stankinschedule.ui.news.review.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.vereshchagin.nikolay.stankinschedule.repository.NewsRepository
import com.vereshchagin.nikolay.stankinschedule.ui.news.review.categories.paging.NewsPostRemoteMediator
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

/**
 * ViewModel для фрагмента с списком новостей.
 *
 * @param repository репозиторий, откуда будут загружаться новости.
 */
@ExperimentalPagingApi
// @HiltViewModel
class NewsPostsViewModel @AssistedInject constructor(
    private val repository: NewsRepository,
    @Assisted private val newsSubdivision: Int,
) : ViewModel() {

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
        remoteMediator = NewsPostRemoteMediator(newsSubdivision, repository)
    ) {
        repository.pagingSource(newsSubdivision)
    }.liveData.cachedIn(viewModelScope)


    /**
     * Factory для создания ViewModel.
     */
    @AssistedFactory
    interface NewsPostsFactory {
        fun create(newsSubdivision: Int): NewsPostsViewModel
    }

    companion object {

        private const val NEWS_PAGE_SIZE = 40

        /**
         * Создает объект в Factory c переданными параметрами.
         */
        fun provideFactory(
            factory: NewsPostsFactory,
            newsSubdivision: Int,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return factory.create(newsSubdivision) as T
            }
        }
    }
}