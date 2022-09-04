package com.vereshchagin.nikolay.stankinschedule.journal.predict.domain.repository

import com.vereshchagin.nikolay.stankinschedule.journal.core.domain.model.SemesterMarks
import com.vereshchagin.nikolay.stankinschedule.journal.core.domain.model.Student

interface JournalPredictRepository {

    fun computeRating(marks: SemesterMarks): Double

    suspend fun rating(
        student: Student,
        loadSemester: suspend (semester: String) -> SemesterMarks,
    ): Double

    suspend fun predictRating(
        student: Student,
        loadSemester: suspend (semester: String) -> SemesterMarks,
    ): Double

}