package com.vereshchagin.nikolay.stankinschedule.model.schedule.repository

import com.google.gson.annotations.SerializedName
import org.joda.time.DateTime
import org.joda.time.Hours
import org.joda.time.LocalDate

/**
 * Класс с описанием данных о репозитории с расписанием.
 */
class RepositoryDescription(
    @SerializedName("last_update")
    val lastUpdate: String,
    @SerializedName("date")
    var date: DateTime = DateTime.now(),
) {
    fun lastUpdateString(): String {
        val lastDate = LocalDate.parse(lastUpdate)
        return lastDate.toString("dd.MM.yyyy")
    }

    fun isValid() = Hours.hoursBetween(date, DateTime.now()).hours < 2
}