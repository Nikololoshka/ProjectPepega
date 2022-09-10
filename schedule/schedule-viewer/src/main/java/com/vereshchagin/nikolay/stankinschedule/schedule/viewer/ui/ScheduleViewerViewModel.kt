package com.vereshchagin.nikolay.stankinschedule.schedule.viewer.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.ScheduleInfo
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.ScheduleModel
import com.vereshchagin.nikolay.stankinschedule.schedule.viewer.domain.model.ScheduleViewDay
import com.vereshchagin.nikolay.stankinschedule.schedule.viewer.domain.usecase.ScheduleViewerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.joda.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ScheduleViewerViewModel @Inject constructor(
    private val useCase: ScheduleViewerUseCase,
    private val handle: SavedStateHandle,
) : ViewModel() {

    private val _scheduleInfo = MutableStateFlow<ScheduleInfo?>(null)
    val scheduleInfo = _scheduleInfo.asStateFlow()

    private val _schedule = MutableStateFlow<ScheduleModel?>(null)
    private val _scheduleStartDay = MutableStateFlow(
        handle.get<LocalDate>(CURRENT_PAGER_DATE) ?: LocalDate.now()
    )

    private val clearListCh = Channel<Unit>(Channel.CONFLATED)

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val scheduleDays: Flow<PagingData<ScheduleViewDay>> =
        flowOf(
            clearListCh.receiveAsFlow().map { PagingData.empty() },
            combine(_schedule, _scheduleStartDay) { model, day -> model to day }
                .flatMapLatest { (model, day) ->
                    if (model != null) useCase.createPager(model, day).flow else emptyFlow()
                }
                .flowOn(Dispatchers.IO).cachedIn(viewModelScope)
        ).flattenMerge(2)

    fun loadSchedule(scheduleId: Long) {
        if (_scheduleInfo.value != null) return

        viewModelScope.launch {
            useCase.scheduleInfo(scheduleId)
                .collect {
                    _scheduleInfo.value = it
                }
        }

        viewModelScope.launch {
            useCase.scheduleModel(scheduleId)
                .collect {
                    clearListCh.send(Unit)
                    _schedule.value = it
                }
        }
    }

    fun selectDate(date: LocalDate) {
        viewModelScope.launch {
            clearListCh.send(Unit)
            _scheduleStartDay.value = date
        }
    }

    /**
     * Устанавливает текущую дату, которая отображается в pager, для
     * ее последующего отображения, если расписание обновится.
     */
    fun updatePagingDate(currentPagingDate: LocalDate?) {
        handle[CURRENT_PAGER_DATE] = currentPagingDate
    }

    companion object {
        private const val CURRENT_PAGER_DATE = "current_pager_date"
    }
}