package com.vereshchagin.nikolay.stankinschedule.ui.schedule.view.pagingkt

import com.vereshchagin.nikolay.stankinschedule.model.schedule.pair.Pair
import org.joda.time.LocalDate

/**
 * Класс дня с парами в просмотре расписания.
 */
data class ScheduleViewDay(
    val pairs: List<Pair>,
    val day: LocalDate
)