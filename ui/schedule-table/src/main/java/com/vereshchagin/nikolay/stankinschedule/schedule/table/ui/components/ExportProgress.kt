package com.vereshchagin.nikolay.stankinschedule.schedule.table.ui.components

import android.net.Uri

sealed interface ExportProgress {
    object Nothing : ExportProgress
    object Running : ExportProgress
    class Finished(
        val path: Uri,
        val type: ExportType,
        val format: ExportFormat
    ) : ExportProgress

    class Error(val error: Throwable) : ExportProgress
}