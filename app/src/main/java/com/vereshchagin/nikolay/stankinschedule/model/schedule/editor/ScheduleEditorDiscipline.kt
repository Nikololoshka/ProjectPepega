package com.vereshchagin.nikolay.stankinschedule.model.schedule.editor

import com.vereshchagin.nikolay.stankinschedule.model.schedule.pair.Pair

data class ScheduleEditorDiscipline(
    val discipline: String,
    val lecturers: List<Pair>,
    val seminars: List<Pair>,
    val labs: List<Pair>,
)