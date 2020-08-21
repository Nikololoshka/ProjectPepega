package com.vereshchagin.nikolay.stankinschedule.model.modulejournal

import org.joda.time.DateTime
import org.joda.time.Minutes
import java.util.*
import kotlin.collections.ArrayList

/**
 * Оценки студента за семестр.
 */
data class SemesterMarks(
    val disciplines: ArrayList<Discipline>,
    var rating: Int?,
    var accumulatedRating: Int?,
    val time: DateTime = DateTime.now()
) {

    /**
     * Добавляет оценку в список оценок за семестр.
     * @param disciplineTitle название предмета.
     * @param type тип оценки.
     * @param value значение оценки.
     * @param factor коэффициент предмета.
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
                discipline.marks[markType] = value
                return
            }
        }

        val discipline = Discipline(disciplineTitle, linkedMapOf(Pair(markType, value)), factor)
        disciplines.add(discipline)
        disciplines.sortWith(Comparator { o1, o2 -> o1.title.compareTo(o2.title) })
    }

    fun createRowData(): List<String> {
        val rowData = arrayListOf<String>()
        for (discipline in disciplines) {
            rowData.add(discipline.title)
        }

        if (rating != null) {
            rowData.add(RATING)
        }
        if (accumulatedRating != null) {
            rowData.add(ACCUMULATED_RATING)
        }

        return rowData
    }

    fun createColumnData() : List<String> {
        return arrayListOf("М1", "М2", "К", "З", "Э", "К")
    }

    fun createCellData(): List<List<String?>> {
        val cellsData = arrayListOf<List<String?>>()
        for (discipline in disciplines) {
            cellsData.add(discipline.createRowCells())
        }

        val count = createColumnData().size
        rating?.let {
            val data = ArrayList<String?>(count)
            data.add(if (it == 0) "" else it.toString())
            for (i in 0 until count - 1) { data.add(null) }
            cellsData.add(data)
        }
        accumulatedRating?.let {
            val data = ArrayList<String?>(count)
            data.add(if (it == 0) "" else it.toString())
            for (i in 0 until count - 1) { data.add(null) }
            cellsData.add(data)
        }

        return cellsData
    }

    fun isValid() : Boolean {
        return Minutes.minutesBetween(DateTime.now(), time).minutes < 30
    }

    companion object {
        const val RATING = "Рейтинг"
        const val ACCUMULATED_RATING = "Накопленный Рейтинг"

        fun fromResponse(response: List<MarkResponse>): SemesterMarks {
            val marks = SemesterMarks(arrayListOf(), null, null)
            for (mark in response) {
                marks.addMark(mark.title, mark.type, mark.value, mark.factor)
            }
            return marks
        }
    }
}