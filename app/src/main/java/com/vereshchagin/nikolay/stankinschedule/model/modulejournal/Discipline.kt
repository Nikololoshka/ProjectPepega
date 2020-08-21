package com.vereshchagin.nikolay.stankinschedule.model.modulejournal

import kotlin.math.abs

/**
 * Дисциплина в модульном журнале.
 */
data class Discipline(
    val title: String,
    val marks: LinkedHashMap<MarkType, Int>,
    val factor: Double
) {

    fun createRowCells(): List<String?> {
        val row = arrayListOf<String?>()

        for (type in MarkType.values()) {

            when (val mark = marks[type]) {
                null -> row.add(null)
                NO_MARK -> row.add("")
                else -> row.add(mark.toString())
            }
        }

        row.add(if(abs(factor) < 2 * Double.MIN_VALUE)  "" else factor.toString())

        return row
    }

    override fun toString(): String {
        return "Discipline(title='$title', marks=$marks, factor=$factor)"
    }

    companion object {
        const val NO_MARK = 0
    }
}