package com.vereshchagin.nikolay.stankinschedule.journal.core.domain.model

import com.google.gson.annotations.SerializedName

/**
 * Дисциплина в модульном журнале.
 */
data class Discipline(
    @SerializedName("title")
    val title: String = "",
    @SerializedName("marks")
    private val marks: LinkedHashMap<MarkType, Int> = linkedMapOf(),
    @SerializedName("factor")
    val factor: Double = NO_FACTOR,
) : Iterable<Pair<MarkType, Int>> {
    /**
     * Строковое представление коэффициента.
     */
    val factorHolder get() = if (factor == NO_FACTOR) " " else factor.toString()

    /**
     * Проверяет, является ли дисциплина завершенной (есть все оценки).
     */
    fun isCompleted(): Boolean {
        for (mark in marks) {
            if (mark.value == NO_MARK) {
                return false
            }
        }
        if (factor == NO_FACTOR) {
            return false
        }
        return true
    }

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

    override fun iterator(): Iterator<Pair<MarkType, Int>> = marks
        .map { (key, value) -> (key to value) }
        .iterator()

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