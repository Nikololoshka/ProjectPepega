package com.vereshchagin.nikolay.stankinschedule.schedule.data.mapper

import com.vereshchagin.nikolay.stankinschedule.schedule.data.api.DescriptionResponse
import com.vereshchagin.nikolay.stankinschedule.schedule.data.api.PairResponse
import com.vereshchagin.nikolay.stankinschedule.schedule.data.api.ScheduleItemResponse
import com.vereshchagin.nikolay.stankinschedule.schedule.data.db.RepositoryEntity
import com.vereshchagin.nikolay.stankinschedule.schedule.domain.model.*


fun ScheduleItemResponse.toItem(category: String): RepositoryItem {
    return RepositoryItem(
        name = name,
        path = path,
        category = category
    )
}

fun RepositoryEntity.toItem(): RepositoryItem {
    return RepositoryItem(
        name = name,
        path = path,
        category = category
    )
}

fun RepositoryItem.toEntity(): RepositoryEntity {
    return RepositoryEntity(
        name = name,
        path = path,
        category = category
    )
}

fun DescriptionResponse.toDescription(): RepositoryDescription {
    return RepositoryDescription(
        lastUpdate = lastUpdate,
        categories = categories.map { RepositoryCategory(it.name, it.year) }
    )
}

fun List<PairResponse>.toScheduleModel(scheduleName: String): ScheduleModel {
    val model = ScheduleModel(ScheduleInfo(scheduleName = scheduleName))
    this.forEach { response ->
        model.add(response.toPairModel())
    }
    return model
}

fun PairResponse.toPairModel(): PairModel {
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

fun PairResponse.DateResponse.toDateItem(): DateItem {
    val f = Frequency.of(frequency)
    if (f == Frequency.ONCE) {
        return DateSingle(date)
    }
    return DateRange(date, f)

}
