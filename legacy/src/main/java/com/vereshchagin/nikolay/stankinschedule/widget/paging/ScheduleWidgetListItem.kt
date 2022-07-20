package com.vereshchagin.nikolay.stankinschedule.widget.paging

import com.vereshchagin.nikolay.stankinschedule.model.schedule.db.PairItem
import org.joda.time.LocalDate

/**
 * Информация о дне виджета с расписанием.
 * @param dayTitle заголовок дня.
 * @param pairs пары дня.
 * @param dayTime дата дня.
 */
class ScheduleWidgetListItem(
    var dayTitle: String,
    var pairs: List<PairItem>,
    var dayTime: LocalDate,
)