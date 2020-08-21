package com.vereshchagin.nikolay.stankinschedule.model.modulejournal

import com.google.gson.annotations.SerializedName

/**
 * Ответ с оценкой о сервера.
 */
class MarkResponse(
    val factor: Double,
    val title: String,
    @SerializedName("num") val type: String,
    val value: Int
) {
    override fun toString(): String {
        return "MarkResponse(factor=$factor, title='$title', type='$type', value=$value)"
    }
}