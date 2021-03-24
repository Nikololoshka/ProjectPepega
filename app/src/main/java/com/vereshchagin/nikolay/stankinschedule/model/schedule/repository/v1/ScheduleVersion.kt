package com.vereshchagin.nikolay.stankinschedule.model.schedule.repository.v1

import com.google.gson.annotations.SerializedName

/**
 * Версия расписания.
 */
class ScheduleVersion(
    @SerializedName("path")
    val path: String,
    @SerializedName("date")
    val date: String,
)