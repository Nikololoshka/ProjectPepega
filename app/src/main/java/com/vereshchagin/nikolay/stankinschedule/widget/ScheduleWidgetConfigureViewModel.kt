package com.vereshchagin.nikolay.stankinschedule.widget

import android.app.Application
import androidx.lifecycle.*
import com.vereshchagin.nikolay.stankinschedule.repository.ScheduleRepositoryKt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 *
 */
class ScheduleWidgetConfigureViewModel(
    application: Application,
) : AndroidViewModel(application) {

    private val repository = ScheduleRepositoryKt(application)
    val schedules = MutableLiveData<List<String>>(emptyList())

    init {
        viewModelScope.launch(Dispatchers.IO) {
            repository.schedules()
                .collect {
                    schedules.postValue(it.map { item -> item.scheduleName })
                }
        }
    }

    /**
     *
     */
    class Factory(
        private val application: Application,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return ScheduleWidgetConfigureViewModel(application) as T
        }
    }
}