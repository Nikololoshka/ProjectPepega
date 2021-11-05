package com.vereshchagin.nikolay.stankinschedule.ui.schedule.repository.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.vereshchagin.nikolay.stankinschedule.model.schedule.remote.ScheduleRepositoryInfo
import com.vereshchagin.nikolay.stankinschedule.repository.ScheduleRemoteRepository
import com.vereshchagin.nikolay.stankinschedule.utils.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel стартовой страницы удаленного репозитория.
 */
@HiltViewModel
class RepositoryOverviewViewModel @Inject constructor(
    private val repository: ScheduleRemoteRepository,
) : ViewModel() {

    /**
     * Описание репозитория.
     */
    private val _description = MutableStateFlow<State<ScheduleRepositoryInfo.Description>>(
        State.loading()
    )
    val description = _description.asStateFlow()

    /**
     * Список категорий.
     */
    val categories = Pager(PagingConfig(40)) {
        repository.rootCategorySource()
    }.flow.cachedIn(viewModelScope)

    init {
        updateRepository(useCache = true)
    }

    /**
     * Обновляет данные репозитория.
     */
    fun updateRepository(useCache: Boolean = true) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.description(useCache)
                .catch { e ->
                    _description.value = State.failed(e)
                }
                .collect { state ->
                    _description.value = state
                }
        }
    }
}