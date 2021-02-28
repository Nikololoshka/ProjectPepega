package com.vereshchagin.nikolay.stankinschedule.ui.schedule.editor.view

import android.app.Application
import androidx.lifecycle.*
import androidx.paging.*
import com.vereshchagin.nikolay.stankinschedule.model.schedule.Schedule
import com.vereshchagin.nikolay.stankinschedule.model.schedule.editor.ScheduleEditorDiscipline
import com.vereshchagin.nikolay.stankinschedule.repository.ScheduleRepository
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.editor.view.paging.ScheduleDisciplineSource
import kotlinx.coroutines.launch

/**
 * ViewModel фрагмента редактирования расписания.
 */
class ScheduleEditorViewModel(
    private val scheduleName: String,
    application: Application,
) : AndroidViewModel(application) {

    private val refreshTrigger = MutableLiveData<Unit?>()

    val disciplines = Transformations.switchMap(refreshTrigger) {
        update()
    }
    private var schedule: Schedule? = null
    private var disciplineList = emptyList<String>()

    init {
        viewModelScope.launch {
            val repository = ScheduleRepository()
            val newSchedule = repository.load(scheduleName, getApplication())

            disciplineList = newSchedule.disciplines()
            schedule = newSchedule

            refreshTrigger.value = null
        }
    }

    private fun update(): LiveData<PagingData<ScheduleEditorDiscipline>> {
        if (schedule == null) {
            return MutableLiveData(null)
        }

        val pager = Pager(PagingConfig(1)) {
            ScheduleDisciplineSource(
                schedule!!, disciplineList
            )
        }
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