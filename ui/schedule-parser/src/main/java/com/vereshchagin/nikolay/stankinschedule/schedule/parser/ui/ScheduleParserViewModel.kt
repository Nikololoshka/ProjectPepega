package com.vereshchagin.nikolay.stankinschedule.schedule.parser.ui

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vereshchagin.nikolay.stankinschedule.core.domain.usecase.DeviceUseCase
import com.vereshchagin.nikolay.stankinschedule.schedule.parser.domain.model.ParseResult
import com.vereshchagin.nikolay.stankinschedule.schedule.parser.domain.usecase.ParserUseCase
import com.vereshchagin.nikolay.stankinschedule.schedule.parser.ui.components.ParserState
import com.vereshchagin.nikolay.stankinschedule.schedule.parser.ui.components.StepState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScheduleParserViewModel @Inject constructor(
    private val deviceUseCase: DeviceUseCase,
    private val parserUseCase: ParserUseCase
) : ViewModel() {

    private val _stepState = MutableStateFlow(StepState(currentStep = 1, totalSteps = 4))
    val stepState = _stepState.asStateFlow()

    private val _parserState = MutableStateFlow<ParserState>(ParserState.SelectFile())
    val parserState = _parserState.asStateFlow()


    fun selectFile(uri: Uri) {
        viewModelScope.launch {
            try {
                val filename = deviceUseCase.extractFilename(uri.toString())
                val preview = parserUseCase.renderPreview(uri.toString())
                val data = ParserState.SelectFileData(uri, filename, preview)
                _parserState.value = ParserState.SelectFile(data)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun startScheduleParser(data: ParserState.SelectFileData) {
        viewModelScope.launch {
            try {
                val result = parserUseCase.parsePDF(data.uri.toString())

                val successResult = mutableListOf<ParseResult.Success>()
                val errorResult = mutableListOf<ParseResult.Error>()
                for (r in result) {
                    if (r is ParseResult.Success) successResult += r
                    if (r is ParseResult.Error) errorResult += r
                }

                _parserState.value = ParserState.ParseSchedule(
                    successResult = successResult,
                    errorResult = errorResult
                )

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    fun back() {
        when (val currentState = _parserState.value) {
            is ParserState.SelectFile -> {

            }

            is ParserState.ParseSchedule -> {
                _parserState.value = ParserState.SelectFile()
                _stepState.value = _stepState.value.back()
            }
        }
    }

    fun next() {
        when (val currentState = _parserState.value) {
            is ParserState.SelectFile -> {
                if (currentState.data != null) {
                    startScheduleParser(currentState.data)
                    _stepState.value = _stepState.value.next()
                }
            }

            is ParserState.ParseSchedule -> {

            }
        }
    }
}