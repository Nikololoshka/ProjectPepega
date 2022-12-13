package com.vereshchagin.nikolay.stankinschedule.journal.core.domain.usecase

import com.vereshchagin.nikolay.stankinschedule.journal.core.domain.model.SemesterMarks
import com.vereshchagin.nikolay.stankinschedule.journal.core.domain.model.Student
import com.vereshchagin.nikolay.stankinschedule.journal.core.domain.repository.JournalRepository
import com.vereshchagin.nikolay.stankinschedule.journal.core.domain.usecase.predict.PredictCalculater
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class PredictUseCase @Inject constructor(
    private val journal: JournalRepository,
) {
    fun rating(student: Student): Flow<String> = flow {
        val rating = PredictCalculater.rating(student) { journal.semesterMarks(it) }
        if (rating.isFinite() && rating > 0.0) {
            emit("%.2f".format(rating))
        } else {
            emit("--.--")
        }
    }.flowOn(Dispatchers.Default)

    fun predictRating(student: Student): Flow<String> = flow {
        val rating = PredictCalculater.predictRating(student) { journal.semesterMarks(it) }
        if (rating.isFinite() && rating > 0.0) {
            emit("%.2f".format(rating))
        } else {
            emit("--.--")
        }
    }.flowOn(Dispatchers.Default)

    fun predictSemester(marks: SemesterMarks): Double {
        return PredictCalculater.computeRating(marks)
    }

    fun semesterMarks(semester: String): Flow<SemesterMarks> = flow {
        val semesterMarks = journal.semesterMarks(semester)
        emit(semesterMarks)
    }.flowOn(Dispatchers.IO)

    fun semesters(): Flow<List<String>> = flow {
        val student = journal.student()
        if (student != null) {
            emit(student.semesters)
        }
    }.flowOn(Dispatchers.IO)
}