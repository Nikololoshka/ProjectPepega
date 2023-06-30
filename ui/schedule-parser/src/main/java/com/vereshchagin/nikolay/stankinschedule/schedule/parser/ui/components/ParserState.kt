package com.vereshchagin.nikolay.stankinschedule.schedule.parser.ui.components

import android.graphics.Bitmap
import android.net.Uri
import com.vereshchagin.nikolay.stankinschedule.schedule.parser.domain.model.ParseResult

interface ParserState {

    val isSuccess: Boolean

    class SelectFile(val data: SelectFileData? = null) : ParserState {
        override val isSuccess: Boolean
            get() = data != null
    }

    class ParseSchedule(
        val successResult: List<ParseResult.Success>,
        val errorResult: List<ParseResult.Error>,
    ) : ParserState {
        override val isSuccess: Boolean
            get() = successResult.isNotEmpty() && errorResult.isNotEmpty()
    }

    class SelectFileData(val uri: Uri, val name: String, val preview: Bitmap? = null)
}