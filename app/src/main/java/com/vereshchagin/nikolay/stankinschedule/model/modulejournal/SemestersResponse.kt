package com.vereshchagin.nikolay.stankinschedule.model.modulejournal

import com.google.gson.annotations.SerializedName

/**
 * Ответ с информацией о студенте и его семестрах от сервера.
 */
class SemestersResponse(
    val surname: String,
    val initials: String,
    @SerializedName("stgroup") val group: String,
    val semesters: List<String>
) {
    val fullname get() = "$surname $initials"

    override fun toString(): String {
        return "SemestersResponse(surname='$surname', initials='$initials', group='$group', semesters=$semesters)"
    }
}