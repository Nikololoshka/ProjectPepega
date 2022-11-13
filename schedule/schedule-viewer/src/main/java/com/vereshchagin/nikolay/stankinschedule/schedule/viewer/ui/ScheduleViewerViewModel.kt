package com.vereshchagin.nikolay.stankinschedule.schedule.viewer.ui

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.ScheduleModel
import com.vereshchagin.nikolay.stankinschedule.schedule.core.ui.data.ScheduleViewDay
import com.vereshchagin.nikolay.stankinschedule.schedule.settings.domain.model.PairColorGroup
import com.vereshchagin.nikolay.stankinschedule.schedule.viewer.domain.usecase.ScheduleViewerUseCase
import com.vereshchagin.nikolay.stankinschedule.schedule.viewer.ui.components.RenameEvent
import com.vereshchagin.nikolay.stankinschedule.schedule.viewer.ui.components.RenameState
import com.vereshchagin.nikolay.stankinschedule.schedule.viewer.ui.components.ScheduleState
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

    val isVerticalViewer: Flow<Boolean> = useCase.isVerticalViewer()
    val pairColorGroup: Flow<PairColorGroup> = useCase.pairColorGroup()

    private val _scheduleState = MutableStateFlow<ScheduleState>(ScheduleState.Loading)
    val scheduleState = _scheduleState.asStateFlow()

    private val clearPager = Channel<Unit>(Channel.CONFLATED)

    val currentDay: LocalDate get() = handle.get<LocalDate>(CURRENT_PAGER_DATE) ?: LocalDate.now()
    private val _scheduleStartDay = MutableStateFlow(currentDay)

    private var _scheduleId: Long = -1
    private val _schedule = MutableStateFlow<ScheduleModel?>(null)

    private val _renameState = MutableStateFlow<RenameState?>(null)
    val renameState = _renameState.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val scheduleDays: Flow<PagingData<ScheduleViewDay>> =
        flowOf(
            clearPager.receiveAsFlow().map { PagingData.empty() },
            combine(_schedule, _scheduleStartDay) { model, _ -> model }
                .flatMapLatest { model ->
                    if (model != null) useCase.createPager(model, currentDay).flow else emptyFlow()
                }
                .flowOn(Dispatchers.IO).cachedIn(viewModelScope)
        ).flattenMerge(2)

    fun loadSchedule(scheduleId: Long, startDate: LocalDate?) {
        // Расписание с таким ID уже загружено
        if (_scheduleId == scheduleId) return

        if (startDate != null) updatePagingDate(startDate)

        viewModelScope.launch {
            useCase.scheduleModel(scheduleId)
                .collect { model ->
                    // нет такого расписания
                    if (model == null) {
                        _scheduleState.value = ScheduleState.NotFound
                    } else {
                        _scheduleState.value = ScheduleState.Success(
                            scheduleName = model.info.scheduleName,
                            isEmpty = model.isEmpty()
                        )

                        clearPager.send(Unit)
                        _schedule.value = model
                        _scheduleId = model.info.id
                    }
                }
        }
    }

    fun selectDate(date: LocalDate) {
        if (date == currentDay) return

        viewModelScope.launch {
            updatePagingDate(date)

            clearPager.send(Unit)
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

    fun onRenameEvent(event: RenameEvent) {
        _renameState.value = when (event) {
            is RenameEvent.Rename -> RenameState.Rename
            is RenameEvent.Cancel -> null
        }
    }

    fun removeSchedule() {
        viewModelScope.launch {
            useCase.removeSchedule(_scheduleId)
            _scheduleState.value = ScheduleState.NotFound
        }
    }

    fun saveToDevice(context: Context, uri: Uri) {
        viewModelScope.launch {
            useCase.saveToDevice(context, _scheduleId, uri)
                .catch { e ->
                    Log.d("MyLog", "saveToDevice: $e")
                }
                .collect { isSave ->
                    Log.d("MyLog", "saveToDevice: $isSave")
                }
        }
    }

    fun renameSchedule(newName: String) {
        viewModelScope.launch {
            useCase.renameSchedule(_scheduleId, newName)
                .catch { e ->
                    _renameState.value = RenameState.Error(e)
                    e.printStackTrace()
                }
                .collect { isRename ->
                    _renameState.value = if (isRename) {
                        RenameState.Success
                    } else {
                        RenameState.AlreadyExist()
                    }
                }
        }
    }

    companion object {
        private const val CURRENT_PAGER_DATE = "current_pager_date"
    }
}