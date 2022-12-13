package com.vereshchagin.nikolay.stankinschedule.schedule.viewer.domain.model

import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.Subgroup
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.Type

data class ScheduleViewPair(
    val id: Long,
    val title: String,
    val lecturer: String,
    val classroom: ViewContent,
    val subgroup: Subgroup,
    val type: Type,
    val startTime: String,
    val endTime: String
)