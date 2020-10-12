package com.vereshchagin.nikolay.stankinschedule.ui.schedule.repository

import android.app.Application
import androidx.lifecycle.*
import androidx.paging.*
import com.vereshchagin.nikolay.stankinschedule.MainApplication
import com.vereshchagin.nikolay.stankinschedule.model.schedule.repository.RepositoryCategoryItem
import com.vereshchagin.nikolay.stankinschedule.model.schedule.repository.RepositoryDescription
import com.vereshchagin.nikolay.stankinschedule.repository.ScheduleServerRepository
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.repository.paging.RepositoryCategorySource
import com.vereshchagin.nikolay.stankinschedule.utils.State
import kotlinx.coroutines.launch

/**
 * ViewModel для фрагмента с репозиторием расписаний.
 */
class ScheduleRepositoryViewModel(application: Application) : AndroidViewModel(application) {

    val description = MutableLiveData<State<RepositoryDescription>>(State.loading())

    private val refreshTrigger = MutableLiveData<Unit>()
    val categories = Transformations.switchMap(refreshTrigger) { refresh() }

    private val repository = ScheduleServerRepository()

    init {
        viewModelScope.launch {
            repository.description(this@ScheduleRepositoryViewModel::descriptionResult)
        }
    }

    private fun descriptionResult(state: State<RepositoryDescription>) {
        description.value = state
        refreshTrigger.value = null
    }

    private fun refresh(): LiveData<PagingData<RepositoryCategoryItem>> {
        val state = description.value
        if (state is State.Success) {
            return Pager(PagingConfig(1), state.data.categories.first()) {
                RepositoryCategorySource(repository, getApplication<MainApplication>().cacheDir)
            }.liveData.cachedIn(viewModelScope)
        }
        return MutableLiveData(null)
    }


    /**
     * Фабрика для создания ViewModel.
     */
    class Factory(
        private val application: Application
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return ScheduleRepositoryViewModel(application) as T
        }
    }
}