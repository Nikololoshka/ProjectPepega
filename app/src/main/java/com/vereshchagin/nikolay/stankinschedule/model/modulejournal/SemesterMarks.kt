package com.vereshchagin.nikolay.stankinschedule.model.modulejournal

import androidx.room.Entity
import com.google.gson.annotations.SerializedName
import org.joda.time.DateTime
import org.joda.time.Minutes

/**
 * Оценки студента за семестр.
 */
@Entity(tableName = "semester_marks")
data class SemesterMarks(
    @SerializedName("disciplines")
    val disciplines: ArrayList<Discipline> = arrayListOf(),
    @SerializedName("rating")
    var rating: Int? = null,
    @SerializedName("accumulatedRating")
    var accumulatedRating: Int? = null,
    @SerializedName("time")
    val time: DateTime = DateTime.now()
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

    /**
     * Рассчитывает рейтинг для данного семестра.
     */
    fun computeRating(): Double {
        var ratingSum = 0.0
        var ratingCount = 0.0
        for (discipline in disciplines) {
            ratingSum += discipline.computeRating()
            ratingCount += discipline.factor
        }
        return ratingSum / ratingCount
    }

    /**
     * Возвращает заголовок таблицы (включая коэффициент).
     */
    fun headerData(): List<String> {
        return arrayListOf("М1", "М2", "К", "З", "Э", "К")
    }

    /**
     * Проверяет, действительны ли оценки.
     */
    fun isValid(): Boolean {
        return Minutes.minutesBetween(DateTime.now(), time).minutes < 60
    }

    companion object {

        const val RATING = "Рейтинг"
        const val ACCUMULATED_RATING = "Накопленный Рейтинг"

        /**
         * Возвращает объект с оценками семестра из ответа от сервера.
         */
        @JvmStatic
        fun fromResponse(response: List<MarkResponse>) = SemesterMarks().apply {
            for (mark in response) {
                addMark(mark.title, mark.type, mark.value, mark.factor)
            }
        }
    }
}