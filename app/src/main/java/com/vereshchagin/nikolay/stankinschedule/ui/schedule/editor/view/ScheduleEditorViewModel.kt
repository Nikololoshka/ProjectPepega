package com.vereshchagin.nikolay.stankinschedule.ui.schedule.editor.view

import android.app.Application
import androidx.lifecycle.*
import androidx.paging.PagingData
import com.vereshchagin.nikolay.stankinschedule.repository.ScheduleRepository
import kotlinx.coroutines.launch

/**
 * ViewModel фрагмента редактирования расписания.
 */
class ScheduleEditorViewModel(
    private val scheduleName: String,
    application: Application,
) : AndroidViewModel(application) {

    val disciplines = MutableLiveData<PagingData<String>>()

    init {
        viewModelScope.launch {
            val repository = ScheduleRepository()
            val schedule = repository.load(scheduleName, getApplication())
            disciplines.value = PagingData.from(schedule.disciplines())
        }
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