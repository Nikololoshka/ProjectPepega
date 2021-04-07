package com.vereshchagin.nikolay.stankinschedule.model.schedule.repository.v1

import com.vereshchagin.nikolay.stankinschedule.utils.extensions.toPrettyDate

/**
 * Объект версии расписания в удаленном репозитории для отображения.
 */
class ScheduleVersionEntry(
    val scheduleName: String,
    val path: String,
    val date: String,
) : RepositoryItem {

    override fun data(): String {
        return "$scheduleName (${date.toPrettyDate()})"
    }

    fun toVersion() = ScheduleVersion(path, date)
}