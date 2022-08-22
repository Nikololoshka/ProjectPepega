package com.vereshchagin.nikolay.stankinschedule.schedule.viewer.domain.model

import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.PairModel
import org.joda.time.LocalDate

/**
 * Класс дня с парами в просмотре расписания.
 */
data class ScheduleViewDay(
    val pairs: List<PairModel>,
    val day: LocalDate,
)