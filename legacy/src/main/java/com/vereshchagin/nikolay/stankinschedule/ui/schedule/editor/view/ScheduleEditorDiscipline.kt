package com.vereshchagin.nikolay.stankinschedule.ui.schedule.editor.view

import com.vereshchagin.nikolay.stankinschedule.model.schedule.db.PairItem

data class ScheduleEditorDiscipline(
    val discipline: String,
    val lecturers: List<PairItem>,
    val seminars: List<PairItem>,
    val labs: List<PairItem>,
)