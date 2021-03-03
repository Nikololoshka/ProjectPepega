package com.vereshchagin.nikolay.stankinschedule.model.home

import com.vereshchagin.nikolay.stankinschedule.model.schedule.pair.Subgroup

/**
 * Настройки расписания на главной странице.
 */
data class HomeScheduleSettings(
    val delta: Int,
    val display: Boolean,
    val subgroup: Subgroup,
    val favorite: String?,
)