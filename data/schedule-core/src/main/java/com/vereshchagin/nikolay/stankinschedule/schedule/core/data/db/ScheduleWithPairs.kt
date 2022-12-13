package com.vereshchagin.nikolay.stankinschedule.schedule.core.data.db

import androidx.room.Embedded
import androidx.room.Relation

/**
 * Сущность расписания со связанными парами в БД.
 * @param schedule элемент расписания В БД.
 * @param pairs список пар расписания в БД.
 */
class ScheduleWithPairs(
    @Embedded
    val schedule: ScheduleEntity,
    @Relation(parentColumn = "id", entityColumn = "schedule_id")
    val pairs: List<PairEntity>,
)