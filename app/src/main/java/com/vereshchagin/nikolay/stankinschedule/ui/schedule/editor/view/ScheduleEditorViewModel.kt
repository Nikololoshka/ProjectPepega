package com.vereshchagin.nikolay.stankinschedule.ui.schedule.editor.view

import android.app.Application
import androidx.lifecycle.*
import androidx.paging.*
import com.vereshchagin.nikolay.stankinschedule.model.schedule.Schedule
import com.vereshchagin.nikolay.stankinschedule.model.schedule.editor.ScheduleEditorDiscipline
import com.vereshchagin.nikolay.stankinschedule.repository.ScheduleRepository
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.editor.view.paging.ScheduleDisciplineSource
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

/**
 * ViewModel фрагмента редактирования расписания.
 */
class ScheduleEditorViewModel(
    private val scheduleName: String,
    application: Application,
) : AndroidViewModel(application) {

    enum class ScheduleState {
        SUCCESSFULLY_LOADED,
        SUCCESSFULLY_LOADED_EMPTY,
        LOADING,
    }

    private val refreshTrigger = MutableLiveData<Unit?>()

    val disciplines = Transformations.switchMap(refreshTrigger) {
        update()
    }
    val state = MutableLiveData(ScheduleState.LOADING)

    private var schedule: Schedule? = null
    private var disciplineList = emptyList<String>()
    private val repository = ScheduleRepository(application)

    init {
        viewModelScope.launch {
            repository.schedule(scheduleName)
                .filterNotNull()
                .collect {
                    disciplineList = it.disciplines()
                    schedule = it
                    refreshTrigger.value = null
                }
        }
    }

    private fun update(): LiveData<PagingData<ScheduleEditorDiscipline>> {
        val currentSchedule = schedule ?: return MutableLiveData(null)

        val pager = Pager(PagingConfig(1)) {
            ScheduleDisciplineSource(
                currentSchedule, disciplineList
            )
        }

        state.postValue(if (currentSchedule.isEmpty()) {
            ScheduleState.SUCCESSFULLY_LOADED_EMPTY
        } else {
            ScheduleState.SUCCESSFULLY_LOADED
        })

        return pager.liveData.cachedIn(viewModelScope)
    }


    /**
     * Фабрика по созданию ViewModel.
     */
    class Factory(
        private val application: Application,
        private val scheduleName: String,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return ScheduleEditorViewModel(scheduleName, application) as T
        }
    }
}