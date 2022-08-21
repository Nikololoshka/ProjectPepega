package com.vereshchagin.nikolay.stankinschedule.schedule.ui.list.components

sealed interface CreateState {
    object New : CreateState
    object AlreadyExist : CreateState
}