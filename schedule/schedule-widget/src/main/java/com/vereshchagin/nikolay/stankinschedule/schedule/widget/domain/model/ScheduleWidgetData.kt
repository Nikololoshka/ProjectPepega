package com.vereshchagin.nikolay.stankinschedule.schedule.widget.domain.model

import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.Subgroup

data class ScheduleWidgetData(
    val scheduleName: String,
    val scheduleId: Long,
    val subgroup: Subgroup,
    val display: Boolean,
)
