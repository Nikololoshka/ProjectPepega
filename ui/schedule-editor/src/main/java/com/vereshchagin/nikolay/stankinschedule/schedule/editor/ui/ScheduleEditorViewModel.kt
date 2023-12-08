package com.vereshchagin.nikolay.stankinschedule.schedule.editor.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.ScheduleModel
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.Type
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.usecase.ScheduleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ScheduleEditorViewModel @Inject constructor(
    private val scheduleUseCase: ScheduleUseCase
) : ViewModel() {

    private val _scheduleName = MutableStateFlow<String?>(null)
    val scheduleName = _scheduleName.asStateFlow()

    private val _disciplines = MutableStateFlow<List<ScheduleEditorDiscipline>>(emptyList())
    val disciplines = _disciplines.asStateFlow()

    fun loadSchedule(scheduleId: Long?) {
        if (scheduleId == null) return

        viewModelScope.launch {
            scheduleUseCase
                .scheduleModel(scheduleId)
                .collect { schedule ->
                    if (schedule != null) {
                        _scheduleName.value = schedule.info.scheduleName
                        _disciplines.value = representSchedule(schedule)
                    }
                }
        }
    }

    private fun representSchedule(schedule: ScheduleModel): List<ScheduleEditorDiscipline> {
        return schedule
            .groupBy { pair -> pair.title }
            .map { (title, pairs) ->
                ScheduleEditorDiscipline(
                    discipline = title,
                    lecturers = pairs.filter { pair -> pair.type == Type.LECTURE },
                    seminars = pairs.filter { pair -> pair.type == Type.SEMINAR },
                    labs = pairs.filter { pair -> pair.type == Type.LABORATORY },
                )
            }
    }

}

