package com.vereshchagin.nikolay.stankinschedule.schedule.creator.ui.components

import android.net.Uri

interface ImportEvent {
    object Cancel : ImportEvent
    class Import(val uri: Uri) : ImportEvent
}