package com.vereshchagin.nikolay.stankinschedule.model.schedule.db

import androidx.room.Embedded
import androidx.room.Relation

class ScheduleWithPairs(
    @Embedded
    val schedule: ScheduleItem,
    @Relation(parentColumn = "id", entityColumn = "schedule_id")
    val pairs: List<PairItem>,
)