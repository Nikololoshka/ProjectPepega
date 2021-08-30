package com.vereshchagin.nikolay.stankinschedule.model.schedule.review

import com.vereshchagin.nikolay.stankinschedule.model.schedule.db.ScheduleItem

data class MyScheduleListItem(
    val id: Long,
    val scheduleName: String,
    val position: Int,
    val isFavorite: Boolean,
    val isSelected: Boolean
) {
    constructor(
        item: ScheduleItem, isFavorite: Boolean, isSelected: Boolean
    ) : this(item.id, item.scheduleName, item.position, isFavorite, isSelected)
}