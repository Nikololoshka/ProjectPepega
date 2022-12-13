package com.vereshchagin.nikolay.stankinschedule.journal.core.domain.model

import com.google.gson.annotations.SerializedName

/**
 * Оценки студента за семестр.
 */
data class SemesterMarks(
    @SerializedName("disciplines")
    private val disciplines: ArrayList<Discipline> = arrayListOf(),
    @SerializedName("rating")
    private var _rating: Int? = null,
    @SerializedName("accumulatedRating")
    private var _accumulatedRating: Int? = null,
) : Iterable<Discipline> {

    val rating: Int? get() = _rating

    val accumulatedRating: Int? get() = _accumulatedRating

    /**
     * Добавляет оценку в список оценок за семестр.
     */
    fun addMark(disciplineTitle: String, type: String, value: Int, factor: Double) {
        if (disciplineTitle == RATING) {
            _rating = value
            return
        }
        if (disciplineTitle == ACCUMULATED_RATING) {
            _accumulatedRating = value
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

    override fun iterator(): Iterator<Discipline> = disciplines.iterator()

    companion object {

        const val RATING = "Рейтинг"
        const val ACCUMULATED_RATING = "Накопленный Рейтинг"
    }
}