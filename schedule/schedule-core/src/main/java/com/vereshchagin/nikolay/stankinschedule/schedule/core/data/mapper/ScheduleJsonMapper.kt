package com.vereshchagin.nikolay.stankinschedule.schedule.core.data.mapper

import com.vereshchagin.nikolay.stankinschedule.schedule.core.data.api.PairJson
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.*

fun PairJson.toPairModel(): PairModel {
    return PairModel(
        title = title,
        lecturer = lecturer,
        classroom = classroom,
        type = Type.of(type),
        subgroup = Subgroup.of(subgroup),
        time = Time(time.start, time.end),
        date = DateModel().apply { date.forEach { add(it.toDateItem()) } }
    )
}

fun PairJson.DateJson.toDateItem(): DateItem {
    val f = Frequency.of(frequency)
    if (f == Frequency.ONCE) {
        return DateSingle(date)
    }
    return DateRange(date, f)
}