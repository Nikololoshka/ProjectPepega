package com.vereshchagin.nikolay.stankinschedule.schedule.data.mapper

import com.vereshchagin.nikolay.stankinschedule.schedule.data.api.DescriptionResponse
import com.vereshchagin.nikolay.stankinschedule.schedule.data.api.ScheduleItemResponse
import com.vereshchagin.nikolay.stankinschedule.schedule.data.db.PairEntity
import com.vereshchagin.nikolay.stankinschedule.schedule.data.db.RepositoryEntity
import com.vereshchagin.nikolay.stankinschedule.schedule.data.db.ScheduleWithPairs
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

fun RepositoryItem.toEntiry(): RepositoryEntity {
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

fun ScheduleWithPairs.toScheduleModel(): ScheduleModel {
    val info = ScheduleInfo(
        id = schedule.id,
        scheduleName = schedule.scheduleName,
        lastUpdate = schedule.lastUpdate,
        position = schedule.position,
        synced = schedule.synced
    )

    val model = ScheduleModel(info)
    for (pair in pairs) {
        model.add(pair.toPairModel())
    }

    return model
}

fun PairEntity.toPairModel(): PairModel {
    return PairModel(
        title = title,
        lecturer = lecturer,
        classroom = classroom,
        type = type,
        subgroup = subgroup,
        time = time,
        date = date
    )
}