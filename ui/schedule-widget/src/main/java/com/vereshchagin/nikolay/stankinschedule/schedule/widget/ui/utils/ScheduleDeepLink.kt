package com.vereshchagin.nikolay.stankinschedule.schedule.widget.ui.utils

import android.content.Intent
import androidx.core.net.toUri
import org.joda.time.LocalDate

object ScheduleDeepLink {

    const val SCHEDULE_VIEWER_ACTION: String =
        "com.vereshchagin.nikolay.stankinschedule.action.SCHEDULE_VIEWER"

    const val DEEP_LINK = "app://stankinschedule.com/schedule/viewer/{scheduleId}?date={startDate}"

    fun viewerIntent(id: Long, date: LocalDate? = null): Intent = Intent(
        SCHEDULE_VIEWER_ACTION,
        if (date == null) {
            "app://stankinschedule.com/schedule/viewer/$id"
        } else {
            "app://stankinschedule.com/schedule/viewer/$id?date=${date.toString("yyyy-MM-dd")}"
        }.toUri(),
    ).apply {
        addCategory(Intent.CATEGORY_DEFAULT)
    }
}