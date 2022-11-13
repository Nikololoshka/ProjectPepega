package com.vereshchagin.nikolay.stankinschedule.schedule.widget.domain.model

data class ScheduleWidgetPair(
    val title: String,
    val classroom: String,
    val time: String,
    val type: ScheduleWidgetPairType
)