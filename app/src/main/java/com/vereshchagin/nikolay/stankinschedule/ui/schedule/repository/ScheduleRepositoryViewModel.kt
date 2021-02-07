package com.vereshchagin.nikolay.stankinschedule.ui.schedule.repository

import android.app.Application
import androidx.lifecycle.*
import androidx.paging.*
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.vereshchagin.nikolay.stankinschedule.model.schedule.repository.RepositoryCategoryItem
import com.vereshchagin.nikolay.stankinschedule.model.schedule.repository.RepositoryDescription
import com.vereshchagin.nikolay.stankinschedule.repository.ScheduleServerRepository
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.repository.paging.RepositoryCategorySource
import com.vereshchagin.nikolay.stankinschedule.utils.State
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * ViewModel для фрагмента с репозиторием расписаний.
 */
class ScheduleRepositoryViewModel(application: Application) : AndroidViewModel(application) {

    val description = MutableLiveData<State<RepositoryDescription>>(State.loading())

    private val refreshTrigger = MutableLiveData(true)
    val categories = Transformations.switchMap(refreshTrigger) { refresh(it) }

    private val repository = ScheduleServerRepository(application.cacheDir)

    init {
        update()
    }

    /**
     * Вызывается, если пришел результат с описанием, и необходимо загружать категории.
     */
    private fun descriptionResult(state: State<RepositoryDescription>, useCache: Boolean) {
        description.value = state
        refreshTrigger.value = useCache
    }

    /**
     * Возвращает название категории по индексу.
     */
    fun tabTitle(position: Int): String? {
        val state = description.value
        if (state is State.Success) {
            return state.data.categories.getOrNull(position)
        }
        return null
    }

    /**
     * Обновляет Pager для отображения категорий в репозитории.
     */
    private fun refresh(useCache: Boolean): LiveData<PagingData<RepositoryCategoryItem>> {
        val state = description.value
        if (state is State.Success) {
            return Pager(PagingConfig(1), state.data.categories.first()) {
                RepositoryCategorySource(repository, state.data.categories, useCache)
            }.liveData.cachedIn(viewModelScope)
        }
        return MutableLiveData(null)
    }

    /**
     * Обновляет содержимое репозитория
     */
    fun update(useCache: Boolean = true) {
        viewModelScope.launch {
            repository.description(useCache)
                .catch { e ->
                    Firebase.crashlytics.recordException(e)
                }
                .collect {
                    descriptionResult(it, useCache)
                }
        }
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