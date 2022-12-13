package com.vereshchagin.nikolay.stankinschedule.schedule.creator.ui.components

sealed interface ImportState {
    class Failed(val error: Throwable) : ImportState
    class Success(val scheduleName: String) : ImportState
}