package com.vereshchagin.nikolay.stankinschedule.schedule.data.mapper

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.vereshchagin.nikolay.stankinschedule.schedule.data.db.PairEntity
import com.vereshchagin.nikolay.stankinschedule.schedule.data.db.ScheduleEntity
import com.vereshchagin.nikolay.stankinschedule.schedule.data.db.ScheduleWithPairs
import com.vereshchagin.nikolay.stankinschedule.schedule.domain.model.*

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

fun ScheduleEntity.toInfo(): ScheduleInfo {
    return ScheduleInfo(
        id = id,
        scheduleName = scheduleName,
        lastUpdate = lastUpdate,
        position = position,
        synced = synced
    )
}

fun ScheduleInfo.toEntity(): ScheduleEntity {
    return ScheduleEntity(scheduleName = scheduleName).apply {
        this.id = this@toEntity.id
        this.lastUpdate = this@toEntity.lastUpdate
        this.position = this@toEntity.position
        this.synced = this@toEntity.synced
    }
}

fun PairEntity.toPairModel(): PairModel {
    return PairModel(
        title = title,
        lecturer = lecturer,
        classroom = classroom,
        type = Type.of(type),
        subgroup = Subgroup.of(subgroup),
        time = Time.fromString(time),
        date = ScheduleJsonUtils.dateFromJson(
            Gson().fromJson(date, JsonElement::class.java)
        )
    )
}

fun ScheduleModel.toEntity(position: Int? = null): ScheduleEntity {
    return ScheduleEntity(info.scheduleName).apply {
        this.id = info.id
        this.lastUpdate = info.lastUpdate
        this.position = position ?: info.position
        this.synced = info.synced
    }
}

fun PairModel.toEntity(scheduleId: Long): PairEntity {
    return PairEntity(
        scheduleId = scheduleId,
        title = title,
        lecturer = lecturer,
        classroom = classroom,
        type = type.tag,
        subgroup = subgroup.tag,
        time = time.toString(),
        date = ScheduleJsonUtils.toJson(date).toString()
    )
}