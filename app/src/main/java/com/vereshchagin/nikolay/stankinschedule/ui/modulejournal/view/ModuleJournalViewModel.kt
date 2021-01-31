package com.vereshchagin.nikolay.stankinschedule.ui.modulejournal.view

import android.app.Application
import androidx.lifecycle.*
import androidx.paging.*
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.vereshchagin.nikolay.stankinschedule.model.modulejournal.SemesterMarks
import com.vereshchagin.nikolay.stankinschedule.model.modulejournal.StudentData
import com.vereshchagin.nikolay.stankinschedule.repository.ModuleJournalRepository
import com.vereshchagin.nikolay.stankinschedule.ui.modulejournal.view.paging.SemesterMarksSource
import com.vereshchagin.nikolay.stankinschedule.utils.State
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * ViewModel фрагмента модульного журнала.
 */
class ModuleJournalViewModel(
    private val repository: ModuleJournalRepository
) : ViewModel() {

    /**
     * Информация о студенте.
     */
    val studentData = MutableLiveData<State<StudentData>>(State.loading())

    /**
     * Прогнозируемый рейтинг.
     */
    val predictedRating = MutableLiveData<String?>(null)

    /**
     * Текущий рейтинг.
     */
    val currentRating = MutableLiveData<String?>(null)

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
            studentData.value = State.loading()
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
        studentData.value = state
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
                    currentRating.value = it
                }
            repository.predictedRating()
                .collect {
                    predictedRating.value = it
                }
        }
    }

    /**
     * Выполняет выход из модульного журнала.
     */
    fun signOut() {
        repository.signOut()
    }

    /**
     * Фабрика для создания ViewModel.
     */
    class Factory(
        private val application: Application
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return ModuleJournalViewModel(
                ModuleJournalRepository(application.cacheDir)
            ) as T
        }
    }

    companion object {
        /**
         * Количество загружаемых страниц.
         */
        const val SEMESTER_PAGE_COUNT = 1
    }
}