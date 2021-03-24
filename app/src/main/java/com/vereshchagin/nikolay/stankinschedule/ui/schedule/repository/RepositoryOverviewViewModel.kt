package com.vereshchagin.nikolay.stankinschedule.ui.schedule.repository

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.liveData
import com.vereshchagin.nikolay.stankinschedule.model.schedule.repository.RepositoryDescription
import com.vereshchagin.nikolay.stankinschedule.repository.ScheduleRemoteRepository
import com.vereshchagin.nikolay.stankinschedule.utils.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * ViewModel стартовой страницы удаленного репозитория.
 */
class RepositoryOverviewViewModel(
    private val repository: ScheduleRemoteRepository,
) : ViewModel() {

    /**
     * Описание репозитория.
     */
    val description = MutableLiveData<State<RepositoryDescription>>(State.loading())

    /**
     * Список категорий.
     */
    val categories = Pager(PagingConfig(40)) {
        repository.categories(null)
    }.liveData.cachedIn(viewModelScope)

    init {
        updateRepository(true)
    }

    /**
     * Обновляет данные репозитория.
     */
    fun updateRepository(useCache: Boolean = true) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.description(useCache)
                .catch { e ->
                    description.postValue(State.failed(e))
                }
                .collect { state ->
                    description.postValue(state)
                }
        }
    }

    /**
     * Фабрика для создания ViewModel.
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