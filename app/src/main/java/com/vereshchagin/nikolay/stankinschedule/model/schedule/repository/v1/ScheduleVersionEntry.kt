package com.vereshchagin.nikolay.stankinschedule.model.schedule.repository.v1

import org.joda.time.LocalDate

class ScheduleVersionEntry(
    val scheduleName: String,
    val path: String,
    val date: String,
) : RepositoryItem {

    override fun data(): String {
        return "$scheduleName (${LocalDate.parse(date).toString("dd.MM.yyyy")})"
    }
}