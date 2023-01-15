package com.vereshchagin.nikolay.stankinschedule.schedule.editor.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.DateItem
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.DateModel
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.PairModel
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.usecase.PairUseCase
import com.vereshchagin.nikolay.stankinschedule.schedule.editor.ui.components.DateRequest
import com.vereshchagin.nikolay.stankinschedule.schedule.editor.ui.components.DateResult
import com.vereshchagin.nikolay.stankinschedule.schedule.editor.ui.components.EditorMode
import com.vereshchagin.nikolay.stankinschedule.schedule.editor.ui.components.PairEditorState
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
    private val useCase: PairUseCase,
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

    fun loadPair(scheduleId: Long, pairId: Long?, mode: EditorMode) {
        this.scheduleId = scheduleId

        if (isInitial) return

        if (mode == EditorMode.Create || pairId == null) {
            _pair.value = PairEditorState.Content(null)
            return
        }

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

    fun applyPair(newPair: PairModel) {
        viewModelScope.launch {
            try {
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