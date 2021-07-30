package com.vereshchagin.nikolay.stankinschedule.model.schedule.repository.v1

import com.vereshchagin.nikolay.stankinschedule.utils.DateTimeUtils
import org.joda.time.LocalDate


/**
 * Объект версии расписания в удаленном репозитории для отображения.
 */
class ScheduleVersionEntry(
    val scheduleName: String,
    val path: String,
    val date: String,
) : RepositoryItem {

    override fun data(): String {
        return "$scheduleName (${
            LocalDate.parse(date).toString(DateTimeUtils.PRETTY_DATE_PATTERN)
        })"
    }

    fun toVersion() = ScheduleVersion(path, date)
}