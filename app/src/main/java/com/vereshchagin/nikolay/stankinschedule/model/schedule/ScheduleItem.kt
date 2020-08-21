package com.vereshchagin.nikolay.stankinschedule.model.schedule

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "schedules")
class ScheduleItem(
    @PrimaryKey val name: String,
    val schedule: Schedule
)