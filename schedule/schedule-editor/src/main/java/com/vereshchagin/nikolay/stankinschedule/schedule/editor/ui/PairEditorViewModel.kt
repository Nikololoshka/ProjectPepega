package com.vereshchagin.nikolay.stankinschedule.schedule.editor.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vereshchagin.nikolay.stankinschedule.core.ui.State
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.DateModel
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.PairModel
import com.vereshchagin.nikolay.stankinschedule.schedule.editor.domain.usecase.PairEditorUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class PairEditorViewModel @Inject constructor(
    private val useCase: PairEditorUseCase
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

    private val _pair = MutableStateFlow<State<PairModel?>>(State.loading())
    val pair = _pair.asStateFlow()

    private val _date = MutableStateFlow(DateModel())
    val date = _date.asStateFlow()

    /**
     * Предназначен во избежании изменения данных при
     * повороте и т.п.
     */
    var isInitial: Boolean = false
        private set

    fun loadPair(pairId: Long?) {
        if (pairId == null || isInitial) return

        viewModelScope.launch {
            val newPair = useCase.pair(pairId)

            _pair.value = State.success(newPair)
            if (newPair != null) {
                _date.value = newPair.date
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
}