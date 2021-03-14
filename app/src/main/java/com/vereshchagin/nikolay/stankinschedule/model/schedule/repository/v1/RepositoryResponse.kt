package com.vereshchagin.nikolay.stankinschedule.model.schedule.repository.v1

import com.google.gson.annotations.SerializedName
import org.joda.time.DateTime

class RepositoryResponse(
    @SerializedName("last_update")
    val lastUpdate: String,
    @SerializedName("categories")
    val categories: List<CategoryEntry>,
    @SerializedName("schedules")
    val schedules: List<ScheduleEntry>,
) {
    val date: DateTime = DateTime.now()
}