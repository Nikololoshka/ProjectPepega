package com.vereshchagin.nikolay.stankinschedule.modulejournal.data.repository

import com.vereshchagin.nikolay.stankinschedule.modulejournal.domain.model.Discipline
import com.vereshchagin.nikolay.stankinschedule.modulejournal.domain.model.MarkType
import com.vereshchagin.nikolay.stankinschedule.modulejournal.domain.model.SemesterMarks
import com.vereshchagin.nikolay.stankinschedule.modulejournal.domain.model.Student
import com.vereshchagin.nikolay.stankinschedule.modulejournal.domain.repository.JournalPredictRepository
import javax.inject.Inject

class JournalPredictRepositoryImpl @Inject constructor() : JournalPredictRepository {

    private fun ratingForDiscipline(discipline: Discipline): Double {
        var disciplineSum = 0.0
        var disciplineCount = 0.0
        for (type in MarkType.values()) {
            discipline[type]?.let { mark ->
                disciplineSum += mark * type.weight
                disciplineCount += type.weight
            }
        }
        return (disciplineSum / disciplineCount) * discipline.factor
    }

    override fun computeRating(marks: SemesterMarks): Double {
        var ratingSum = 0.0
        var ratingCount = 0.0

        for (discipline in marks) {
            ratingSum += ratingForDiscipline(discipline)
            ratingCount += discipline.factor
        }

        val rating: Double = ratingSum / ratingCount
        return if (rating.isFinite()) rating else 0.0
    }

    override suspend fun rating(
        student: Student,
        loadSemester: suspend (semester: String) -> SemesterMarks,
    ): Double {
        for (semester in student.semesters) {
            val marks = loadSemester(semester)
            if (marks.isCompleted()) {
                return computeRating(marks)
            }
        }

        return 0.0
    }

    private fun averageRatingForDiscipline(discipline: Discipline): Pair<Int, Int> {
        var disciplineSum = 0
        var disciplineCount = 0
        for ((_, mark) in discipline) {
            if (mark != Discipline.NO_MARK) {
                disciplineSum += mark
                disciplineCount++
            }
        }
        return disciplineSum to disciplineCount
    }

    private fun averageRating(marks: SemesterMarks): Int {
        var ratingSum = 0
        var ratingCount = 0
        for (discipline in marks) {
            val (disciplineSum, disciplineCount) = averageRatingForDiscipline(discipline)
            ratingSum += disciplineSum
            ratingCount += disciplineCount
        }
        return ratingSum / ratingCount
    }

    private fun predictedRatingForDiscipline(discipline: Discipline, averageRating: Int): Double {
        var disciplineSum = 0.0
        var disciplineCount = 0.0
        for (type in MarkType.values()) {
            discipline[type]?.let { mark ->
                disciplineSum += if (mark == Discipline.NO_MARK) {
                    averageRating * type.weight
                } else {
                    mark * type.weight
                }
                disciplineCount += type.weight
            }
        }
        return (disciplineSum / disciplineCount) * discipline.factor
    }

    private fun predictedRating(marks: SemesterMarks, averageRating: Int): Double {
        var ratingSum = 0.0
        var ratingCount = 0.0
        for (discipline in marks) {
            ratingSum += predictedRatingForDiscipline(discipline, averageRating)
            ratingCount += discipline.factor
        }

        val rating: Double = ratingSum / ratingCount
        return if (rating.isFinite()) rating else 0.0
    }

    override suspend fun predictRating(
        student: Student,
        loadSemester: suspend (semester: String) -> SemesterMarks,
    ): Double {
        if (student.semesters.isEmpty()) return 0.0

        val lastSemester = student.semesters.first()
        val lastSemesterMarks = loadSemester(lastSemester)

        // накопленный рейтинг
        var accumulatedRating = 0
        for (i in 1 until student.semesters.size - 1) {
            val semester = student.semesters[i]
            val rating = loadSemester(semester).accumulatedRating
            if (rating != null) {
                accumulatedRating = rating
                break
            }
        }

        // отсутствует накопленный рейтинг (первый семестр)
        if (accumulatedRating == 0) {
            val average = averageRating(lastSemesterMarks)
            if (average == 0) return 0.0
            accumulatedRating = average
        }

        return predictedRating(lastSemesterMarks, accumulatedRating)
    }
}