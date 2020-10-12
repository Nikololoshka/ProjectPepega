package com.vereshchagin.nikolay.stankinschedule.model.schedule.repository

import com.google.gson.annotations.SerializedName

/**
 * Класс с описанием данных о репозитории с расписанием.
 */
class RepositoryDescription (
    @SerializedName("last_update") val lastUpdate: String,
    val categories: List<String>
)