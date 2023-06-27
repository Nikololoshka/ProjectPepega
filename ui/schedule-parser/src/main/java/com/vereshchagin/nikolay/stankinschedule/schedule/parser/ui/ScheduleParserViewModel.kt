package com.vereshchagin.nikolay.stankinschedule.schedule.parser.ui

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.vereshchagin.nikolay.stankinschedule.schedule.parser.ui.components.ParserState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ScheduleParserViewModel : ViewModel() {

    private val _parserState = MutableStateFlow<ParserState>(ParserState.SelectFile())
    val parserState = _parserState.asStateFlow()


    fun selectFile(uri: Uri) {
        _parserState.value = ParserState.SelectFile(uri)
    }

}