package com.vereshchagin.nikolay.stankinschedule.ui.schedule.repository

import android.app.Application
import androidx.lifecycle.*
import androidx.paging.*
import com.vereshchagin.nikolay.stankinschedule.model.schedule.repository.v1.CategoryEntry
import com.vereshchagin.nikolay.stankinschedule.repository.ScheduleRemoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 *
 */
class RepositoryOverviewViewModel(
    private val repository: ScheduleRemoteRepository,
) : ViewModel() {

    private val refreshTrigger = MutableLiveData(null)

    /**
     *
     */
    val categories = Transformations.switchMap(refreshTrigger) {
        updateCategories()
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
//            repository.loadRepositoryEntry()
            refreshTrigger.postValue(null)
        }
    }

    /**
     *
     */
    private fun updateCategories(): LiveData<PagingData<CategoryEntry>> {
        return Pager(PagingConfig(40)) {
            repository.categories(null)
        }.liveData.cachedIn(viewModelScope)
    }

    /**
     *
     */
    class Factory(
        private val application: Application,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return RepositoryOverviewViewModel(
                ScheduleRemoteRepository(application)
            ) as T
        }
    }
}