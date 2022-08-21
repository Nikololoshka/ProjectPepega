package com.vereshchagin.nikolay.stankinschedule.schedule.ui.viewer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.vereshchagin.nikolay.stankinschedule.schedule.domain.model.ScheduleInfo
import com.vereshchagin.nikolay.stankinschedule.schedule.domain.model.ScheduleModel
import com.vereshchagin.nikolay.stankinschedule.schedule.domain.model.ScheduleViewDay
import com.vereshchagin.nikolay.stankinschedule.schedule.domain.usecase.ScheduleViewerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScheduleViewerViewModel @Inject constructor(
    private val useCase: ScheduleViewerUseCase,
) : ViewModel() {

    private val _scheduleInfo = MutableStateFlow<ScheduleInfo?>(null)
    val scheduleInfo = _scheduleInfo.asStateFlow()

    private val _schedule = MutableStateFlow<ScheduleModel?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val scheduleDays: Flow<PagingData<ScheduleViewDay>> = _schedule.flatMapLatest {
        if (it != null) useCase.createPager(it).flow else emptyFlow()
    }.flowOn(Dispatchers.IO).cachedIn(viewModelScope)

    fun loadSchedule(scheduleId: Long) {
        viewModelScope.launch {
            useCase.scheduleInfo(scheduleId)
                .collect {
                    _scheduleInfo.value = it
                }
        }

        viewModelScope.launch {
            useCase.scheduleModel(scheduleId)
                .collect {
                    _schedule.value = it
                }
        }
    }
}