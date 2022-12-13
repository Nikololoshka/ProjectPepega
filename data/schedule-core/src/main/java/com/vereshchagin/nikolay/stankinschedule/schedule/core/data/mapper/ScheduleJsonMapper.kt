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

fun DateModel.toJson(): List<PairJson.DateJson> {
    return this.map { date ->
        PairJson.DateJson(date.frequency().tag, date.toString())
    }
}

fun PairModel.toJson() : PairJson {
    return PairJson(
        title = title,
        lecturer = lecturer,
        classroom = classroom,
        type = type.tag,
        subgroup = subgroup.tag,
        time = PairJson.TimeJson(time.startString(), time.endString()),
        date = date.toJson()
    )
}

fun ScheduleModel.toJson() : List<PairJson> {
    return this.map { pair -> pair.toJson() }
}