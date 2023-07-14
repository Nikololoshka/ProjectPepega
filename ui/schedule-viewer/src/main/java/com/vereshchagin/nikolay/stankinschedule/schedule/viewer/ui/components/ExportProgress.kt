package com.vereshchagin.nikolay.stankinschedule.schedule.viewer.ui.components

import android.net.Uri

sealed interface ExportProgress {
    object Nothing : ExportProgress
    class Finished(val path: Uri, val format: ExportFormat) : ExportProgress
    class Error(val error: Throwable) : ExportProgress
}