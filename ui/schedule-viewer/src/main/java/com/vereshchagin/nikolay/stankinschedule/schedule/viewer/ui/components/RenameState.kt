package com.vereshchagin.nikolay.stankinschedule.schedule.viewer.ui.components

sealed interface RenameState {
    object Rename : RenameState
    class Error(val error: Throwable) : RenameState
    class AlreadyExist : RenameState
    object Success : RenameState
}