package com.vereshchagin.nikolay.stankinschedule.schedule.list.ui

import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.ScheduleInfo
import com.vereshchagin.nikolay.stankinschedule.schedule.list.domain.usecase.ScheduleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScheduleScreenViewModel @Inject constructor(
    private val useCase: ScheduleUseCase,
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
            useCase.schedules()
                .collect { newSchedules ->
                    if (!_editableMode.value) {
                        _schedules.value = newSchedules
                    }
                }
        }
        viewModelScope.launch {
            useCase.favorite()
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
                useCase.updatePositions(data)
            }
        }
    }

    fun schedulesMove(from: Int, to: Int) {
        _schedules.value = _schedules.value?.toMutableList()?.apply { add(to, removeAt(from)) }

        val t = selected[to]
        selected[to] = selected[from] ?: false
        selected[from] = t ?: false
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
            useCase.setFavorite(id)
        }
    }

    fun selectSchedule(id: Long) {
        val index = id.toInt()
        selected[index] = !selected.getOrDefault(index, false)
    }

    fun removeSelectedSchedules() {
        val data = _schedules.value ?: return
        val removed = data.filter { selected.containsKey(it.id.toInt()) }

        _editableMode.value = false
        selected.clear()

        viewModelScope.launch {
            useCase.removeSchedules(removed)
        }
    }

    fun removeSchedule(schedule: ScheduleInfo) {
        viewModelScope.launch {
            useCase.removeSchedules(listOf(schedule))
        }
    }
}
