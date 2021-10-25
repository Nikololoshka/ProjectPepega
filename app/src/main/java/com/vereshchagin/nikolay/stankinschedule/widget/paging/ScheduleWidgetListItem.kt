package com.vereshchagin.nikolay.stankinschedule.widget.paging

import com.vereshchagin.nikolay.stankinschedule.model.schedule.pair.Pair
import org.joda.time.LocalDate

/**
 * Информация о дне виджета с расписанием.
 * @param dayTitle заголовок дня.
 * @param pairs пары дня.
 * @param dayTime дата дня.
 */
class ScheduleWidgetListItem(
    var dayTitle: String,
    var pairs: List<Pair>,
    var dayTime: LocalDate,
)