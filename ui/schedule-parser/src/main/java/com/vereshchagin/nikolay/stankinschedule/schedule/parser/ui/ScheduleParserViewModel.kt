package com.vereshchagin.nikolay.stankinschedule.schedule.parser.ui

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vereshchagin.nikolay.stankinschedule.core.domain.usecase.DeviceUseCase
import com.vereshchagin.nikolay.stankinschedule.schedule.parser.domain.usecase.ParserUseCase
import com.vereshchagin.nikolay.stankinschedule.schedule.parser.ui.components.ParserState
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

    private val _parserState = MutableStateFlow<ParserState>(ParserState.SelectFile())
    val parserState = _parserState.asStateFlow()


    fun selectFile(uri: Uri) {
        try {
            val filename = deviceUseCase.extractFilename(uri.toString())
            val data = ParserState.SelectFileData(uri, filename)
            _parserState.value = ParserState.SelectFile(data)
        } catch (e: Exception) {

        }
    }

    private fun startScheduleParser(data: ParserState.SelectFileData) {
        viewModelScope.launch {
            try {
                val result = parserUseCase.parsePDF(data.uri.toString())
                result.forEach {
                    Log.d("ScheduleParserViewModel", "startScheduleParser: $it")
                }

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
            }
        }
    }

    fun next() {
        when (val currentState = _parserState.value) {
            is ParserState.SelectFile -> {
                if (currentState.data != null) {
                    startScheduleParser(currentState.data)
                }
            }

            is ParserState.ParseSchedule -> {

            }
        }
    }
}