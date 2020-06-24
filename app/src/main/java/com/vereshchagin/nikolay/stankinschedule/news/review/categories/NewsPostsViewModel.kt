package com.vereshchagin.nikolay.stankinschedule.news.review.categories

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vereshchagin.nikolay.stankinschedule.news.review.categories.repository.NewsRepository

/**
 * ViewModel для фрагмента с списком новостей.
 * @param repository репозиторий, откуда будут загружаться новости.
 */
class NewsPostsViewModel(private val repository: NewsRepository) : ViewModel() {

    private val test = MutableLiveData<Int>()
    private val repositoryListing = Transformations.map(test) {
        repository.posts()
    }

    val posts = Transformations.switchMap(repositoryListing) { it.pagedList }
    val networkState = Transformations.switchMap(repositoryListing) { it.networkState }
    val refreshState = Transformations.switchMap(repositoryListing) { it.refreshState }

    init {
        test.value = 1
    }

    /**
     * Обновляет новости.
     */
    fun refresh() {
        repositoryListing.value?.refresh?.invoke()
    }

    /**
     * Попробавать сново загрузить данные, если были ошибки.
     */
    fun retry() {
        val listing = repositoryListing.value
        listing?.retry?.invoke()
    }

    /**
     * Factory для создания ViewModel.
     */
    class Factory(
        private val newsSubdivision: Int, private val context: Context
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return NewsPostsViewModel(NewsRepository(newsSubdivision, context)) as T
        }
    }
}