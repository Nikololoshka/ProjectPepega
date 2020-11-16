package com.vereshchagin.nikolay.stankinschedule.model.modulejournal

import com.google.gson.annotations.SerializedName
import org.joda.time.DateTime
import org.joda.time.Minutes

/**
 * Информация о студенте.
 */
class StudentData(
    @SerializedName("student") val student: String,
    @SerializedName("group") val group: String,
    @SerializedName("semesters") val semesters: List<String>,
    @SerializedName("time") val time: DateTime = DateTime.now()
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