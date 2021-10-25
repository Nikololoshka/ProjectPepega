package com.vereshchagin.nikolay.stankinschedule.ui.modulejournal.view

import androidx.lifecycle.*
import androidx.paging.*
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.vereshchagin.nikolay.stankinschedule.model.modulejournal.SemesterMarks
import com.vereshchagin.nikolay.stankinschedule.model.modulejournal.StudentData
import com.vereshchagin.nikolay.stankinschedule.repository.ModuleJournalRepository
import com.vereshchagin.nikolay.stankinschedule.ui.modulejournal.view.paging.SemesterMarksSource
import com.vereshchagin.nikolay.stankinschedule.utils.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel фрагмента модульного журнала.
 */
@HiltViewModel
class ModuleJournalViewModel @Inject constructor(
    private val repository: ModuleJournalRepository,
) : ViewModel() {

    private val _studentData = MutableStateFlow<State<StudentData>>(State.loading())
    private val _predictedRating = MutableStateFlow<String?>(null)
    private val _currentRating = MutableStateFlow<String?>(null)

    /**
     * Информация о студенте.
     */
    val studentData = _studentData.asStateFlow()

    /**
     * Прогнозируемый рейтинг.
     */
    val predictedRating = _predictedRating.asStateFlow()

    /**
     * Текущий рейтинг.
     */
    val currentRating = _currentRating.asStateFlow()

    /**
     * Вспомогательная LiveData для обновления списка семестров с оценками.
     */
    private val refreshTrigger = MutableLiveData(true)

    /**
     * Список семестров с оценками.
     */
    val semesters = Transformations.switchMap(refreshTrigger) { refreshSemesters(it) }

    init {
        refreshModuleJournal()
    }

    /**
     * Возвращает название семестра с оценками.
     */
    fun tabTitle(position: Int): String? {
        val state = studentData.value
        if (state is State.Success) {
            // индексы отражены наоборот, т.к. pager справа - налево
            return state.data.semesters.getOrNull(state.data.semesters.size - 1 - position)
        }
        return null
    }

    /**
     * Обновляет информацию о студенте.
     */
    fun refreshModuleJournal(useCache: Boolean = true, afterError: Boolean = false) {
        if (afterError) {
            _studentData.value = State.loading()
        }

        viewModelScope.launch {
            repository.studentData(useCache)
                .catch { e ->
                    Firebase.crashlytics.recordException(e)
                }
                .collect {
                    studentDateResult(it, useCache)
                }
        }
    }

    /**
     * Вызывается, если пришел результат с данными студента и
     * необходимо загружать список семестров с оценками.
     */
    private fun studentDateResult(state: State<StudentData>, useCache: Boolean) {
        _studentData.value = state
        refreshTrigger.value = useCache
    }

    /**
     * Обновляет Pager для отображения семестров с оценками.
     */
    private fun refreshSemesters(useCache: Boolean): LiveData<PagingData<SemesterMarks>> {
        val state = studentData.value
        if (state is State.Success) {
            refreshRatings()
            return Pager(
                PagingConfig(SEMESTER_PAGE_COUNT, enablePlaceholders = true),
                initialKey = state.data.semesters.last()
            ) {
                SemesterMarksSource(repository, state.data.semesters, useCache)
            }.liveData.cachedIn(viewModelScope)
        }
        return MutableLiveData(null)
    }

    /**
     * Обновляет расчеты текущего и прогнозируемого рейтинга.
     */
    private fun refreshRatings() {
        viewModelScope.launch {
            repository.currentRating()
                .collect {
                    _currentRating.value = it
                }
            repository.predictedRating()
                .collect {
                    _predictedRating.value = it
                }
        }
    }

    /**
     * Выполняет выход из модульного журнала.
     */
    fun signOut() {
        repository.signOut()
    }

    companion object {
        /**
         * Количество загружаемых страниц.
         */
        const val SEMESTER_PAGE_COUNT = 1
    }
}