package com.vereshchagin.nikolay.stankinschedule.model.modulejournal

import com.google.gson.annotations.SerializedName

/**
 * Дисциплина в модульном журнале.
 */
data class Discipline(
    @SerializedName("title") val title: String = "",
    @SerializedName("marks") val marks: LinkedHashMap<MarkType, Int> = linkedMapOf(),
    @SerializedName("factor") val factor: Double = NO_FACTOR
) {
    /**
     * Строковое представление коэффициента.
     */
    val factorString = if (factor == NO_FACTOR) " " else factor.toString()

    /**
     * Получение оценки по типу.
     */
    operator fun get(type: MarkType): Int? {
        return marks[type]
    }

    /**
     * Установление оценки по типу.
     */
    operator fun set(type: MarkType, value: Int) {
        marks[type] = value
    }

    override fun toString(): String {
        return "Discipline(title='$title', marks=$marks, factor=$factor)"
    }

    companion object {
        /**
         * Отсутствует оценка.
         */
        const val NO_MARK = 0

        /**
         * Отсутствует коэффициента дисциплины.
         */
        const val NO_FACTOR = 0.0
    }
}