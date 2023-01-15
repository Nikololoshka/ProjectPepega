package com.vereshchagin.nikolay.stankinschedule.schedule.list.ui

import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.ScheduleInfo
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.usecase.ScheduleUseCase
import com.vereshchagin.nikolay.stankinschedule.schedule.settings.domain.usecase.ScheduleSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScheduleScreenViewModel @Inject constructor(
    private val scheduleUseCase: ScheduleUseCase,
    private val settingsUseCase: ScheduleSettingsUseCase
) : ViewModel() {

    private val _editableMode = MutableStateFlow(false)
    val editableMode = _editableMode.asStateFlow()

    private val _schedules = MutableStateFlow<List<ScheduleInfo>?>(null)
    val schedules: StateFlow<List<ScheduleInfo>?> = _schedules.asStateFlow()

    private val _favorite = MutableStateFlow<Long>(-1)
    val favorite = _favorite.asStateFlow()

    @Stable
    val selected = mutableStateMapOf<Int, Boolean>()

    init {
        viewModelScope.launch {
            scheduleUseCase.schedules()
                .collect { newSchedules ->
                    if (!_editableMode.value) {
                        _schedules.value = newSchedules
                    }
                }
        }
        viewModelScope.launch {
            settingsUseCase.favorite()
                .collect { newFavorite ->
                    _favorite.value = newFavorite
                }
        }
    }

    private fun isSchedulesMoved(list: List<ScheduleInfo>): Boolean {
        for ((index, schedule) in list.withIndex()) {
            if (schedule.position != index) {
                return true
            }
        }
        return false
    }

    private fun saveSchedulePositions() {
        val data = _schedules.value ?: return

        viewModelScope.launch {
            if (isSchedulesMoved(data)) {
                scheduleUseCase.updatePositions(data)
            }
        }
    }

    fun schedulesMove(from: Int, to: Int) {
        _schedules.value = _schedules.value?.let {
            it.toMutableList().apply { add(to, removeAt(from)) }
        }

        /*
        val t = selected[to]
        selected[to] = selected[from] ?: false
        selected[from] = t ?: false
         */
    }

    fun setEditable(enable: Boolean) {
        _editableMode.value = enable
        selected.clear()

        if (!enable) {
            saveSchedulePositions()
        }
    }

    fun setFavorite(id: Long) {
        viewModelScope.launch {
            settingsUseCase.setFavorite(id)
        }
    }

    fun isSelected(id: Long): Boolean {
        return selected.getOrElse(id.toInt()) { false }
    }

    fun selectSchedule(id: Long) {
        val index = id.toInt()
        selected[index] = !selected.getOrElse(index) { false }
    }

    fun removeSelectedSchedules() {
        val data = _schedules.value ?: return
        val removed = data.filter { selected.containsKey(it.id.toInt()) }

        _editableMode.value = false
        selected.clear()

        viewModelScope.launch {
            scheduleUseCase.removeSchedules(removed)
        }
    }

    fun removeSchedule(schedule: ScheduleInfo) {
        viewModelScope.launch {
            scheduleUseCase.removeSchedules(listOf(schedule))
        }
    }
}
