package com.vereshchagin.nikolay.stankinschedule.ui.schedule.view.paging

import com.vereshchagin.nikolay.stankinschedule.model.schedule.db.PairItem
import org.joda.time.LocalDate

/**
 * Класс дня с парами в просмотре расписания.
 */
data class ScheduleViewDay(
    val pairs: List<PairItem>,
    val day: LocalDate,
)