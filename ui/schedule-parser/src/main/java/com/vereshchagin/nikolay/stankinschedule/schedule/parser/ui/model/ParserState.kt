package com.vereshchagin.nikolay.stankinschedule.schedule.parser.ui.model

import android.graphics.Bitmap
import com.vereshchagin.nikolay.stankinschedule.schedule.parser.domain.model.ParseResult
import com.vereshchagin.nikolay.stankinschedule.schedule.parser.domain.model.ParserSettings
import com.vereshchagin.nikolay.stankinschedule.schedule.table.domain.model.ScheduleTable

interface ParserState {

    val isSuccess: Boolean get() = true

    class SelectFile(
        val file: SelectedFile? = null,
        val preview: Bitmap? = null
    ) : ParserState {
        override val isSuccess: Boolean get() = file != null
    }

    class Settings(val settings: ParserSettings) : ParserState

    class ParserResult(
        val successResult: List<ParseResult.Success>,
        val missingResult: List<ParseResult.Missing>,
        val errorResult: List<ParseResult.Error>,
        val table: ScheduleTable
    ) : ParserState
}