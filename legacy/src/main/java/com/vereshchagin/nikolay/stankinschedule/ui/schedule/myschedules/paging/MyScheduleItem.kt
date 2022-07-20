package com.vereshchagin.nikolay.stankinschedule.ui.schedule.myschedules.paging

import com.vereshchagin.nikolay.stankinschedule.model.schedule.db.ScheduleItem

/**
 * Элемент расписания в списке.
 */
class MyScheduleItem(
    item: ScheduleItem,
) : ScheduleItem(item) {

    /**
     * Выбран ли элемент в списке.
     */
    var isSelected = false
}