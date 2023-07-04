package com.vereshchagin.nikolay.stankinschedule.schedule.parser.ui.components

import android.graphics.Bitmap
import android.net.Uri
import com.vereshchagin.nikolay.stankinschedule.schedule.parser.domain.model.ParseResult
import com.vereshchagin.nikolay.stankinschedule.schedule.table.domain.model.ScheduleTable

interface ParserState {

    val isSuccess: Boolean

    class SelectFile(
        val uri: Uri? = null,
        val name: String? = null,
        val preview: Bitmap? = null
    ) : ParserState {
        override val isSuccess: Boolean get() = uri != null && name != null
    }

    class ParseSchedule(
        val successResult: List<ParseResult.Success>,
        val errorResult: List<ParseResult.Error>,
        val table: ScheduleTable
    ) : ParserState {

        override val isSuccess: Boolean
            get() = successResult.isNotEmpty() && errorResult.isNotEmpty()
    }

}