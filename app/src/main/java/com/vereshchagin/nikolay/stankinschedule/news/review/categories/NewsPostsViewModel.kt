package com.vereshchagin.nikolay.stankinschedule.news.review.categories

import android.app.Application
import androidx.lifecycle.*
import com.vereshchagin.nikolay.stankinschedule.news.review.categories.repository.NewsRepository
import com.vereshchagin.nikolay.stankinschedule.settings.NewsPreference
import com.vereshchagin.nikolay.stankinschedule.utils.DateUtils
import java.util.*

/**
 * ViewModel для фрагмента с списком новостей.
 * @param repository репозиторий, откуда будут загружаться новости.
 * @param application объект приложение для доступа к контексту.
 */
class NewsPostsViewModel(
    private val repository: NewsRepository,
    private val newsSubdivision: Int,
    application: Application
) : AndroidViewModel(application) {

    private val repositoryListing = MutableLiveData(repository.posts())
    val posts = Transformations.switchMap(repositoryListing) { it.pagedList }
    val networkState = Transformations.switchMap(repositoryListing) { it.networkState }
    val refreshState = Transformations.switchMap(repositoryListing) { it.refreshState }
    var startRefreshing = false

    init {
        val date = NewsPreference.lastNewsUpdate(application, newsSubdivision)
        if (date == null || DateUtils.minutesBetween(Calendar.getInstance(), date) > 15) {
            refresh()
        }
    }

    /**
     * Вызывается, если новости были обновлены.
     */
    fun newsUpdated() {
        startRefreshing = false
        NewsPreference.setNewsUpdate(getApplication(), newsSubdivision, Calendar.getInstance())
    }

    /**
     * Обновляет новости.
     */
    fun refresh() {
        startRefreshing = true
        repositoryListing.value?.refresh?.invoke()
    }

    /**
     * Попробавать сново загрузить данные, если были ошибки.
     */
    fun retry() {
        repositoryListing.value?.retry?.invoke()
    }

    /**
     * Factory для создания ViewModel.
     */
    class Factory(
        private val newsSubdivision: Int, private val application: Application
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return NewsPostsViewModel(
                NewsRepository(newsSubdivision, application.applicationContext),
                newsSubdivision,
                application
            ) as T
        }
    }
}