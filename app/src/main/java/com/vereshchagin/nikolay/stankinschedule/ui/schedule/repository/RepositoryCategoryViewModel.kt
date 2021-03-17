package com.vereshchagin.nikolay.stankinschedule.ui.schedule.repository

import android.app.Application
import androidx.lifecycle.*
import androidx.paging.*
import com.vereshchagin.nikolay.stankinschedule.model.schedule.repository.RepositoryItem
import com.vereshchagin.nikolay.stankinschedule.repository.ScheduleRemoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 *
 */
class RepositoryCategoryViewModel(
    private val repository: ScheduleRemoteRepository,
    private val parentCategory: Int,
) : ViewModel() {

    private val refreshTrigger = MutableLiveData<Boolean>(null)

    /**
     *
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
     *
     */
    private fun updateCategories(isScheduleCategory: Boolean?): LiveData<PagingData<RepositoryItem>> {
        if (isScheduleCategory == null) {
            return MutableLiveData(PagingData.empty())
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
     *
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