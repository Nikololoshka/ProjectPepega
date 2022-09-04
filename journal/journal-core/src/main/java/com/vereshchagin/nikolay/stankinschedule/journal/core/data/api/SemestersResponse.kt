package com.vereshchagin.nikolay.stankinschedule.journal.core.data.api

import com.google.gson.annotations.SerializedName

/**
 * Ответ с информацией о студенте и его семестрах от сервера.
 */
class SemestersResponse(
    @SerializedName("surname") val surname: String,
    @SerializedName("initials") val initials: String,
    @SerializedName("stgroup") val group: String,
    @SerializedName("semesters") val semesters: List<String>,
) {
    override fun toString(): String {
        return "SemestersResponse(surname='$surname', initials='$initials', group='$group', semesters=$semesters)"
    }
}