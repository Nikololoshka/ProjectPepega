package com.vereshchagin.nikolay.stankinschedule.schedule.parser.ui

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vereshchagin.nikolay.stankinschedule.core.domain.usecase.DeviceUseCase
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.ScheduleInfo
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.ScheduleModel
import com.vereshchagin.nikolay.stankinschedule.schedule.parser.domain.model.ParseResult
import com.vereshchagin.nikolay.stankinschedule.schedule.parser.domain.usecase.ParserUseCase
import com.vereshchagin.nikolay.stankinschedule.schedule.parser.ui.components.ParserState
import com.vereshchagin.nikolay.stankinschedule.schedule.parser.ui.components.StepState
import com.vereshchagin.nikolay.stankinschedule.schedule.table.domain.model.ScheduleTable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScheduleParserViewModel @Inject constructor(
    private val deviceUseCase: DeviceUseCase,
    private val parserUseCase: ParserUseCase,
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
                _parserState.value = ParserState.SelectFile(uri, filename, preview)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun startScheduleParser(path: Uri, name: String) {
        viewModelScope.launch {
            try {
                val result = parserUseCase.parsePDF(path.toString())

                val successResult = mutableListOf<ParseResult.Success>()
                val errorResult = mutableListOf<ParseResult.Error>()
                for (r in result) {
                    if (r is ParseResult.Success) successResult += r
                    if (r is ParseResult.Error) errorResult += r
                }

                val schedule = ScheduleModel(info = ScheduleInfo(name))
                successResult.forEach {
                    try {
                        schedule.add(it.pair)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                _parserState.value = ParserState.ParseSchedule(
                    successResult = successResult,
                    errorResult = errorResult,
                    table = ScheduleTable(schedule)
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
                if (currentState.uri != null && currentState.name != null) {
                    startScheduleParser(currentState.uri, currentState.name)
                    _stepState.value = _stepState.value.next()
                }
            }

            is ParserState.ParseSchedule -> {

            }
        }
    }
}