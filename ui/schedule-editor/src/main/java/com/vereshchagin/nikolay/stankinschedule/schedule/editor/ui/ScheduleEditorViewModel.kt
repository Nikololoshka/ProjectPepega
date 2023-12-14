package com.vereshchagin.nikolay.stankinschedule.schedule.editor.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.ScheduleModel
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.Type
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.usecase.ScheduleUseCase
import com.vereshchagin.nikolay.stankinschedule.schedule.settings.domain.model.PairColorGroup
import com.vereshchagin.nikolay.stankinschedule.schedule.settings.domain.usecase.ScheduleSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ScheduleEditorViewModel @Inject constructor(
    private val scheduleUseCase: ScheduleUseCase,
    private val scheduleSettingsUseCase: ScheduleSettingsUseCase
) : ViewModel() {

    private val _scheduleColors = scheduleSettingsUseCase.pairColorGroup()
    val scheduleColors = _scheduleColors.stateIn(
        viewModelScope, SharingStarted.Lazily, PairColorGroup.default()
    )

    private val _scheduleName = MutableStateFlow<String?>(null)
    val scheduleName = _scheduleName.asStateFlow()

    private val _disciplines = MutableStateFlow<Map<String, List<ScheduleDiscipline>>>(emptyMap())
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

    private fun representSchedule(schedule: ScheduleModel): Map<String, List<ScheduleDiscipline>> {
        return schedule
            .groupBy { pair -> pair.title }
            .toSortedMap()
            .mapValues { (title, pairs) ->
                val data = mutableListOf<ScheduleDiscipline>()
                for (type in Type.values()) {
                    data += ScheduleDiscipline.ScheduleTypeDiscipline(type, "$title-${type.tag}")
                    data += pairs
                        .filter { pair -> pair.type == type }
                        .map {
                            ScheduleDiscipline.SchedulePairDiscipline(
                                it,
                                "title-${it.info.id}"
                            )
                        }
                }
                data
            }
    }
}

