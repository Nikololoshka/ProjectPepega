package com.vereshchagin.nikolay.stankinschedule.schedule.widget.ui.configure

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vereshchagin.nikolay.stankinschedule.schedule.widget.domain.model.ScheduleItem
import com.vereshchagin.nikolay.stankinschedule.schedule.widget.domain.model.ScheduleWidgetData
import com.vereshchagin.nikolay.stankinschedule.schedule.widget.domain.usecase.ScheduleConfigureUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ScheduleWidgetConfigureViewModel @Inject constructor(
    private val useCase: ScheduleConfigureUseCase
) : ViewModel() {

    private val _schedules = MutableStateFlow(emptyList<ScheduleItem>())
    val schedules: StateFlow<List<ScheduleItem>> = _schedules.asStateFlow()

    private val _currentData = MutableStateFlow<ScheduleWidgetData?>(null)
    val currentData = _currentData.asStateFlow()


    init {
        viewModelScope.launch {
            useCase.schedules().collect { list ->
                _schedules.value = list
            }
        }
    }

    fun loadConfigure(appWidgetId: Int) {
        if (_currentData.value != null) return
        _currentData.value = useCase.loadWidgetData(appWidgetId)
    }

    fun saveConfigure(appWidgetId: Int, data: ScheduleWidgetData) {
        useCase.saveWidgetData(appWidgetId, data)
    }
}
