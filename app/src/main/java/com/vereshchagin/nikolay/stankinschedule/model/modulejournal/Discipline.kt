package com.vereshchagin.nikolay.stankinschedule.model.modulejournal

import com.google.gson.annotations.SerializedName

/**
 * Дисциплина в модульном журнале.
 */
data class Discipline(
    @SerializedName("title")
    val title: String = "",
    @SerializedName("marks")
    val marks: LinkedHashMap<MarkType, Int> = linkedMapOf(),
    @SerializedName("factor")
    val factor: Double = NO_FACTOR
) {
    /**
     * Строковое представление коэффициента.
     */
    val factorString = if (factor == NO_FACTOR) " " else factor.toString()

    /**
     * Вычисляет рейтинг для дисциплины.
     */
    fun computeRating(): Double {
        var disciplineSum = 0.0
        var disciplineCount = 0.0
        for (type in MarkType.values()) {
            marks[type]?.let { mark ->
                disciplineSum += mark * type.weight
                disciplineCount += type.weight
            }
        }
        return (disciplineSum / disciplineCount) * factor
    }

    /**
     * Вычисляет прогнозируемый рейтинг для дисциплины.
     * @param averageRating средний рейтинг для отсутствующих оценок.
     */
    fun computePredictedRating(averageRating: Int): Double {
        var disciplineSum = 0.0
        var disciplineCount = 0.0
        for (type in MarkType.values()) {
            marks[type]?.let { mark ->
                disciplineSum += if (mark == NO_MARK) {
                    averageRating * type.weight
                } else {
                    mark * type.weight
                }
                disciplineCount += type.weight
            }
        }
        return (disciplineSum / disciplineCount) * factor
    }

    /**
     * Возвращает сумму и количество проставленных оценок для
     * вычисления средней оценки.
     */
    fun prepareAverage(): Pair<Int, Int> {
        var disciplineSum = 0
        var disciplineCount = 0
        for (mark in marks) {
            if (mark.value != NO_MARK) {
                disciplineSum += mark.value
                disciplineCount++
            }
        }
        return disciplineSum to disciplineCount
    }

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