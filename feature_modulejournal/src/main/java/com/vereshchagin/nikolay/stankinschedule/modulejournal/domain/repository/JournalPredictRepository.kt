package com.vereshchagin.nikolay.stankinschedule.modulejournal.domain.repository

import com.vereshchagin.nikolay.stankinschedule.modulejournal.domain.model.SemesterMarks
import com.vereshchagin.nikolay.stankinschedule.modulejournal.domain.model.Student

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