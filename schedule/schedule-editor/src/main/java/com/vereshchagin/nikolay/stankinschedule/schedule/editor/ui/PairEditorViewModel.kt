package com.vereshchagin.nikolay.stankinschedule.schedule.editor.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vereshchagin.nikolay.stankinschedule.core.ui.UIState
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.*
import com.vereshchagin.nikolay.stankinschedule.schedule.editor.domain.usecase.PairEditorUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class PairEditorViewModel @Inject constructor(
    private val useCase: PairEditorUseCase,
) : ViewModel() {

    private val _pickerResults = Channel<DateResult>(
        capacity = 1,
        onBufferOverflow = BufferOverflow.DROP_LATEST
    )
    val pickerResults = _pickerResults.receiveAsFlow()

    private val _pickerRequests = Channel<DateRequest>(
        capacity = 1,
        onBufferOverflow = BufferOverflow.DROP_LATEST
    )
    val pickerRequests = _pickerRequests.receiveAsFlow()

    private val _scheduleErrors = Channel<Exception>(
        capacity = 1,
        onBufferOverflow = BufferOverflow.DROP_LATEST
    )
    val scheduleErrors = _scheduleErrors.receiveAsFlow()

    private val _pair = MutableStateFlow<PairEditorState>(PairEditorState.Loading)
    val pair = _pair.asStateFlow()

    private val _date = MutableStateFlow(DateModel())
    val date = _date.asStateFlow()

    private var scheduleId: Long = -1

    /**
     * Предназначен во избежании изменения данных при
     * повороте и т.п.
     */
    var isInitial: Boolean = false
        private set

    fun loadPair(scheduleId: Long, pairId: Long?) {
        this.scheduleId = scheduleId

        if (pairId == null || isInitial) return

        viewModelScope.launch {
            val newPair = useCase.pair(pairId)

            _pair.value = PairEditorState.Content(newPair)
            if (newPair != null) {
                _date.value = newPair.date.clone()
            }
        }
    }

    fun setPairInitial() {
        isInitial = true
    }

    fun onDateRequest(request: DateRequest) {
        viewModelScope.launch {
            _pickerRequests.send(request)
        }
    }

    fun onDateResult(result: DateResult) {
        viewModelScope.launch {
            _pickerResults.send(result)
        }
    }

    fun editDate(old: DateItem, new: DateItem) {
        _date.value = _date.value.clone().apply {
            remove(old)
            add(new)
        }
    }

    fun newDate(new: DateItem) {
        _date.value = _date.value.clone().apply {
            add(new)
        }
    }

    fun removeDate(date: DateItem) {
        _date.value = _date.value.clone().apply {
            remove(date)
        }
    }

    fun applyPair(
        title: String,
        lecturer: String,
        classroom: String,
        type: Type,
        subgroup: Subgroup,
        startTime: String,
        endTime: String,
    ) {
        viewModelScope.launch {
            try {
                val newPair = PairModel(
                    title = title,
                    lecturer = lecturer,
                    classroom = classroom,
                    type = type,
                    subgroup = subgroup,
                    time = Time(startTime, endTime),
                    date = _date.value,
                )

                val pair = _pair.value.getOrNull()

                useCase.changePair(scheduleId, pair, newPair)
                _pair.value = PairEditorState.Complete

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _scheduleErrors.send(e)
            }
        }
    }

    fun deletePair() {
        viewModelScope.launch {
            try {
                val pair = _pair.value.getOrNull()
                if (pair != null) {
                    useCase.deletePair(pair)
                }
                _pair.value = PairEditorState.Complete

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _scheduleErrors.send(e)
            }
        }
    }

    private fun PairEditorState.getOrNull(): PairModel? {
        return if (this is PairEditorState.Content) pair else null
    }
}