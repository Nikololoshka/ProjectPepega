package com.vereshchagin.nikolay.stankinschedule.model.modulejournal

import com.google.gson.annotations.SerializedName
import org.joda.time.DateTime
import org.joda.time.Minutes

/**
 * Информация о студенте.
 */
class StudentData(
    @SerializedName("student")
    val student: String,
    @SerializedName("group")
    val group: String,
    @SerializedName("semesters")
    val semesters: List<String>,
    @SerializedName("time")
    val time: DateTime = DateTime.now(),
) {

    /**
     * Проверяет, являются ли данные о студенте актуальными.
     */
    fun isValid(): Boolean {
        return Minutes.minutesBetween(time, DateTime.now()).minutes < 60 * 8
    }

    override fun toString(): String {
        return "StudentData(student='$student', group='$group', semesters=$semesters, time=$time)"
    }

    companion object {
        /**
         * Возвращает объект с информацией о студенте из ответа от сервера.
         */
        @JvmStatic
        fun fromResponse(response: SemestersResponse) =
            StudentData(response.fullname, response.group, response.semesters)
    }
}