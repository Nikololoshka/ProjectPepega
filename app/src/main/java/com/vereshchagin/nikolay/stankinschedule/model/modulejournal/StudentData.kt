package com.vereshchagin.nikolay.stankinschedule.model.modulejournal

import org.joda.time.DateTime
import org.joda.time.Minutes

/**
 * Информация о студенте.
 */
class StudentData(
    val student: String,
    val group: String,
    val semesters: List<String>,
    val time: DateTime = DateTime.now()
) {
    constructor(
        response: SemestersResponse
    ) : this(response.fullname, response.group, response.semesters)

    fun isValid() : Boolean {
        return Minutes.minutesBetween(DateTime.now(), time).minutes < 90
    }

    override fun toString(): String {
        return "StudentData(student='$student', group='$group', semesters=$semesters, time=$time)"
    }
}