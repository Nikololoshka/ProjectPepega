package com.vereshchagin.nikolay.stankinschedule.schedule.widget.utils

import android.content.Intent
import androidx.core.net.toUri

object ScheduleDeepLink {

    const val SCHEDULE_VIEWER_ACTION: String =
        "com.vereshchagin.nikolay.stankinschedule.action.SCHEDULE_VIEWER"

    fun viewerIntent(id: Long): Intent = Intent(
        SCHEDULE_VIEWER_ACTION,
        "app://stankinschedule.com/schedule/viewer/$id".toUri(),
    ).apply {
        addCategory(Intent.CATEGORY_DEFAULT)
    }
}