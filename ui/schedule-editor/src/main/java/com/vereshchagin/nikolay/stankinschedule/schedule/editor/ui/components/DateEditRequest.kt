package com.vereshchagin.nikolay.stankinschedule.schedule.editor.ui.components

import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.DateItem

sealed interface DateEditorRequest {
    class Edit(val date: DateItem) : DateEditorRequest
    object New : DateEditorRequest
}