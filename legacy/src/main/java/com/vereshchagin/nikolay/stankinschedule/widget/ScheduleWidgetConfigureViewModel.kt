package com.vereshchagin.nikolay.stankinschedule.widget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vereshchagin.nikolay.stankinschedule.model.schedule.db.ScheduleItem
import com.vereshchagin.nikolay.stankinschedule.repository.ScheduleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel для активности по созданию виджета с расписанием.
 */
@HiltViewModel
class ScheduleWidgetConfigureViewModel @Inject constructor(
    val repository: ScheduleRepository,
) : ViewModel() {

    private val _schedules = MutableStateFlow<List<ScheduleItem>>(emptyList())

    /**
     * Список с расписаниями в приложении.
     */
    val schedules = _schedules.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            repository.schedules()
                .collect { list ->
                    _schedules.value = list
                }
        }
    }
}