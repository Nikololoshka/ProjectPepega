package com.vereshchagin.nikolay.stankinschedule.model.schedule.remote

import com.google.gson.annotations.SerializedName
import org.joda.time.DateTime
import org.joda.time.Hours
import org.joda.time.LocalDate

/**
 * Класс с описанием данных о репозитории с расписанием.
 */
class ScheduleRepositoryInfo(
    @SerializedName("description")
    val description: Description,
    @SerializedName("categories")
    val categories: List<ScheduleCategoryEntry>,
) {
    /**
     * Описание репозитория.
     */
    data class Description(
        @SerializedName("version")
        private val version: String,
        @SerializedName("time")
        var time: DateTime? = null,
    ) {

        val lastUpdate: String get() = LocalDate.parse(version).toString("dd.MM.yyyy")

        fun isValid() = Hours.hoursBetween(time, DateTime.now()).hours < 12
    }
}