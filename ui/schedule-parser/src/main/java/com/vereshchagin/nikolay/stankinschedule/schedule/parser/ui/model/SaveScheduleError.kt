package com.vereshchagin.nikolay.stankinschedule.schedule.parser.ui.model

sealed interface SaveScheduleError {

    object InvalidScheduleName : SaveScheduleError

    object ScheduleNameAlreadyExists : SaveScheduleError

}