package com.vereshchagin.nikolay.stankinschedule.schedule.viewer.ui.components

sealed interface ScheduleState {
    object Loading : ScheduleState
    object NotFound : ScheduleState
    class Success(val scheduleName: String, val isEmpty: Boolean) : ScheduleState
}
