package com.vereshchagin.nikolay.stankinschedule.ui.schedule.repository

import android.app.Application
import androidx.lifecycle.*
import androidx.paging.*
import com.vereshchagin.nikolay.stankinschedule.model.schedule.repository.v1.RepositoryItem
import com.vereshchagin.nikolay.stankinschedule.repository.ScheduleRemoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * ViewModel для категории в удаленном репозитории.
 */
class RepositoryCategoryViewModel(
    private val repository: ScheduleRemoteRepository,
    private val parentCategory: Int,
) : ViewModel() {

    private val refreshTrigger = MutableLiveData<Boolean>(null)

    /**
     * Список категорий.
     */
    val categories = Transformations.switchMap(refreshTrigger) {
        updateCategories(it)
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val exist = repository.isScheduleCategory(parentCategory)
            refreshTrigger.postValue(exist)
        }
    }

    /**
     * Обновляет источник данных с категориями расписаний.
     */
    @Suppress("UNCHECKED_CAST")
    private fun updateCategories(isScheduleCategory: Boolean?): LiveData<PagingData<RepositoryItem>> {
        if (isScheduleCategory == null) {
            return MutableLiveData(null)
        }

        return if (isScheduleCategory) {
            Pager(PagingConfig(40)) {
                repository.schedules(parentCategory)
            }.liveData.cachedIn(viewModelScope)

        } else {
            Pager(PagingConfig(40)) {
                repository.categories(parentCategory)
            }.liveData.cachedIn(viewModelScope)
        } as LiveData<PagingData<RepositoryItem>>
    }

    /**
     * Фабрика для создания ViewModel.
     */
    class Factory(
        private val application: Application,
        private val parentCategory: Int,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return RepositoryCategoryViewModel(
                ScheduleRemoteRepository(application), parentCategory
            ) as T
        }
    }
}