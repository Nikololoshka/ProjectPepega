package com.vereshchagin.nikolay.stankinschedule.schedule.list.ui.components

sealed interface CreateState {
    object New : CreateState
    object AlreadyExist : CreateState
}