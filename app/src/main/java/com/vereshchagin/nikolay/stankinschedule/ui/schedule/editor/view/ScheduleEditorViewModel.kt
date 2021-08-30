package com.vereshchagin.nikolay.stankinschedule.ui.schedule.editor.view

import android.app.Application
import androidx.lifecycle.*
import androidx.paging.*
import com.vereshchagin.nikolay.stankinschedule.model.schedule.Schedule
import com.vereshchagin.nikolay.stankinschedule.model.schedule.editor.ScheduleEditorDiscipline
import com.vereshchagin.nikolay.stankinschedule.repository.ScheduleRepository
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.editor.view.paging.ScheduleDisciplineSource
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

/**
 * ViewModel фрагмента редактирования расписания.
 */
@HiltViewModel
class ScheduleEditorViewModel @AssistedInject constructor(
    application: Application,
    private val repository: ScheduleRepository,
    @Assisted private val scheduleId: Long,
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

    init {
        viewModelScope.launch {
            repository.schedule(scheduleId)
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
     * Factory для создания ViewModel.
     */
    @AssistedFactory
    interface ScheduleEditorFactory {
        fun create(scheduleId: Long): ScheduleEditorViewModel
    }

    companion object {
        /**
         * Создает объект в Factory c переданными параметрами.
         */
        fun provideFactory(
            factory: ScheduleEditorFactory,
            scheduleId: Long,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return factory.create(scheduleId) as T
            }
        }
    }
}