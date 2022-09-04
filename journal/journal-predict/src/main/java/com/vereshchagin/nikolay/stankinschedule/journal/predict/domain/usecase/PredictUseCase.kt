package com.vereshchagin.nikolay.stankinschedule.journal.predict.domain.usecase

import com.vereshchagin.nikolay.stankinschedule.journal.core.domain.model.SemesterMarks
import com.vereshchagin.nikolay.stankinschedule.journal.core.domain.repository.JournalRepository
import com.vereshchagin.nikolay.stankinschedule.journal.predict.domain.repository.JournalPredictRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class PredictUseCase @Inject constructor(
    private val journal: JournalRepository,
    private val predict: JournalPredictRepository,
) {

    fun predictSemester(marks: SemesterMarks): Double {
        return predict.computeRating(marks)
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