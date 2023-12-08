package com.vereshchagin.nikolay.stankinschedule.schedule.editor.ui

import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.PairModel
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.Type


data class ScheduleEditorDiscipline(
    val discipline: String,
    val lecturers: List<PairModel>,
    val seminars: List<PairModel>,
    val labs: List<PairModel>,
) : Iterable<Map.Entry<Type, List<PairModel>>> {

    override fun iterator(): Iterator<Map.Entry<Type, List<PairModel>>> =
        mapOf(
            Type.LECTURE to lecturers,
            Type.SEMINAR to seminars,
            Type.LABORATORY to labs
        ).iterator()
}