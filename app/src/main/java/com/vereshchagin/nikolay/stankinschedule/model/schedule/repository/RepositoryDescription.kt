package com.vereshchagin.nikolay.stankinschedule.model.schedule.repository

import com.google.gson.annotations.SerializedName
import org.joda.time.DateTime

/**
 * Класс с описанием данных о репозитории с расписанием.
 */
class RepositoryDescription (
    @SerializedName("last_update") val lastUpdate: String,
    val categories: List<String>,
    var date: DateTime = DateTime.now()
)