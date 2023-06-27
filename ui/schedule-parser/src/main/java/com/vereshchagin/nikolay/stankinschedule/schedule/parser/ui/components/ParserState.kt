package com.vereshchagin.nikolay.stankinschedule.schedule.parser.ui.components

import android.net.Uri

interface ParserState {
    class SelectFile(val uri: Uri? = null) : ParserState
    object ParseSchedule : ParserState
}