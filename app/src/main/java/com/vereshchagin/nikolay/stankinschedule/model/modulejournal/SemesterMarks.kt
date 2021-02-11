package com.vereshchagin.nikolay.stankinschedule.model.modulejournal

import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.google.gson.JsonParseException
import com.google.gson.annotations.SerializedName
import org.joda.time.DateTime
import org.joda.time.Minutes

/**
 * Оценки студента за семестр.
 */
data class SemesterMarks(
    @SerializedName("disciplines")
    val disciplines: ArrayList<Discipline> = arrayListOf(),
    @SerializedName("rating")
    var rating: Int? = null,
    @SerializedName("accumulatedRating")
    var accumulatedRating: Int? = null,
    @SerializedName("time")
    val time: DateTime = DateTime.now(),
) {
    /**
     * Добавляет оценку в список оценок за семестр.
     */
    fun addMark(disciplineTitle: String, type: String, value: Int, factor: Double) {
        if (disciplineTitle == RATING) {
            rating = value
            return
        }
        if (disciplineTitle == ACCUMULATED_RATING) {
            accumulatedRating = value
            return
        }

        if (disciplineTitle == "Государственный экзамен" && type.trim().isEmpty()) {
            return
        }

        val markType = MarkType.of(type)
        for (discipline in disciplines) {
            if (discipline.title == disciplineTitle) {
                discipline[markType] = value
                return
            }
        }

        val discipline = Discipline(disciplineTitle, linkedMapOf(Pair(markType, value)), factor)
        disciplines.add(discipline)
        disciplines.sortWith { o1, o2 -> o1.title.compareTo(o2.title) }
    }

    fun updateMark(disciplineName: String, type: MarkType, mark: Int) {
        for (discipline in disciplines) {
            if (discipline.title == disciplineName) {
                discipline[type] = mark
                break
            }
        }
    }

    /**
     * Рассчитывает рейтинг для данного семестра.
     */
    fun computeRating(): Double {
        try {
            var ratingSum = 0.0
            var ratingCount = 0.0
            for (discipline in disciplines) {
                ratingSum += discipline.computeRating()
                ratingCount += discipline.factor
            }

            val rating: Double = ratingSum / ratingCount
            return if (rating.isFinite()) rating else 0.0

        } catch (e: Exception) {
            Firebase.crashlytics.recordException(e)
        }

        return 0.0
    }

    fun computePredictedRating(averageRating: Int): Double {
        try {
            var ratingSum = 0.0
            var ratingCount = 0.0
            for (discipline in disciplines) {
                ratingSum += discipline.computePredictedRating(averageRating)
                ratingCount += discipline.factor
            }

            val rating: Double = ratingSum / ratingCount
            return if (rating.isFinite()) rating else 0.0

        } catch (e: Exception) {
            Firebase.crashlytics.recordException(e)
        }

        return 0.0
    }

    /**
     * Вычисляет среднюю оценку в семестре.
     */
    fun average(): Int {
        try {
            var ratingSum = 0
            var ratingCount = 0
            for (discipline in disciplines) {
                val (disciplineSum, disciplineCount) = discipline.prepareAverage()
                ratingSum += disciplineSum
                ratingCount += disciplineCount
            }
            return ratingSum / ratingCount

        } catch (e: Exception) {
            Firebase.crashlytics.recordException(e)
        }

        return 0
    }

    /**
     * Проверяет, является ли семестр завершенным (есть все оценки).
     */
    fun isCompleted(): Boolean {
        for (disciple in disciplines) {
            if (!disciple.isCompleted()) {
                return false
            }
        }
        return true
    }

    /**
     * Проверяет, действительны ли оценки.
     */
    fun isValid(last: Boolean = false): Boolean {
        return Minutes.minutesBetween(time, DateTime.now()).minutes < 60 +
            if (last) 0 else 60 * 24 * 7
    }

    companion object {

        const val RATING = "Рейтинг"
        const val ACCUMULATED_RATING = "Накопленный Рейтинг"

        /**
         * Возвращает объект с оценками семестра из ответа от сервера.
         */
        @JvmStatic
        fun fromResponse(response: List<MarkResponse>) = SemesterMarks().apply {
            try {
                for (mark in response) {
                    addMark(mark.title, mark.type, mark.value, mark.factor)
                }
            } catch (e: Throwable) {
                throw JsonParseException("Invalid parse response: $response", e)
            }
        }
    }
}