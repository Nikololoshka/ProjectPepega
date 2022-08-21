package com.vereshchagin.nikolay.stankinschedule.schedule.domain.model

import org.joda.time.LocalDate

/**
 * Класс дня с парами в просмотре расписания.
 */
data class ScheduleViewDay(
    val pairs: List<PairModel>,
    val day: LocalDate,
)