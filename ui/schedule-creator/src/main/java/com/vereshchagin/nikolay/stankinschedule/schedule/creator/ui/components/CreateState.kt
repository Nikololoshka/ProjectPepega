package com.vereshchagin.nikolay.stankinschedule.schedule.creator.ui.components

sealed interface CreateState {
    object New : CreateState
    class Error(val error: Throwable) : CreateState
    class AlreadyExist : CreateState
    object Success : CreateState
}