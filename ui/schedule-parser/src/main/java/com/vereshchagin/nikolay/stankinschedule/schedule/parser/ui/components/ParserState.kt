package com.vereshchagin.nikolay.stankinschedule.schedule.parser.ui.components

import android.net.Uri

interface ParserState {

    class SelectFile(val data: SelectFileData? = null) : ParserState

    object ParseSchedule : ParserState

    class SelectFileData(val uri: Uri, val name: String)
}