package com.vereshchagin.nikolay.stankinschedule.schedule.widget.domain.model

import org.joda.time.LocalDate


data class ScheduleWidgetDay(
    val day: String,
    val date: LocalDate,
    val pairs: List<ScheduleWidgetPair>
)