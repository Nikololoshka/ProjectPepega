package com.vereshchagin.nikolay.stankinschedule.model.modulejournal

import androidx.room.Entity
import com.google.gson.annotations.SerializedName

/**
 * Дисциплина в модульном журнале.
 */
@Entity(tableName = "discipline")
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