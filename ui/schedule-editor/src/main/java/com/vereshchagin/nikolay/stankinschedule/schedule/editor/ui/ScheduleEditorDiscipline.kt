package com.vereshchagin.nikolay.stankinschedule.schedule.editor.ui

import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.PairModel
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.Type


sealed class ScheduleDiscipline(val key: String) {

    class ScheduleTypeDiscipline(
        val type: Type,
        key: String
    ) : ScheduleDiscipline(key)

    class SchedulePairDiscipline(
        val pair: PairModel,
        key: String
    ) : ScheduleDiscipline(key)
}