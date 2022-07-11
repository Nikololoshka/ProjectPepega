package com.vereshchagin.nikolay.stankinschedule.ui.schedule.repository.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.vereshchagin.nikolay.stankinschedule.model.schedule.remote.ScheduleCategoryEntry
import com.vereshchagin.nikolay.stankinschedule.model.schedule.remote.ScheduleRepositoryItem
import com.vereshchagin.nikolay.stankinschedule.repository.ScheduleRemoteRepository
import com.vereshchagin.nikolay.stankinschedule.utils.State
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * ViewModel для категории в удаленном репозитории.
 */
// @HiltViewModel
class RepositoryCategoryViewModel @AssistedInject constructor(
    private val repository: ScheduleRemoteRepository,
    @Assisted private val parentCategory: Int,
) : ViewModel() {

    private var _entries = MutableStateFlow<Flow<PagingData<ScheduleRepositoryItem>>>(
        flowOf(PagingData.empty())
    )
    private val _categoryState = MutableStateFlow<State<ScheduleCategoryEntry>>(State.loading())


    /**
     * Список категорий.
     */
    @OptIn(FlowPreview::class)
    val entries = _entries.asStateFlow().flattenMerge(1)

    /**
     * Состояние текущей категории.
     */
    val categoryState = _categoryState.asStateFlow()

    init {
        // получение источника категорий
        viewModelScope.launch(Dispatchers.IO) {
            val isNode = repository.isScheduleCategory(parentCategory)
            _entries.value = Pager(PagingConfig(40)) {
                repository.categorySource(isNode, parentCategory)
            }.flow
        }
        updateCategory()
    }

    fun updateCategory() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.refreshCategory(parentCategory).collect { state ->
                _categoryState.value = state
            }
        }
    }

    /**
     * Factory для создания ViewModel.
     */
    @AssistedFactory
    interface RepositoryCategoryFactory {
        fun create(parentCategory: Int): RepositoryCategoryViewModel
    }

    companion object {
        /**
         * Создает объект в Factory c переданными параметрами.
         */
        fun provideFactory(
            factory: RepositoryCategoryFactory,
            parentCategory: Int,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return factory.create(parentCategory) as T
            }
        }
    }
}