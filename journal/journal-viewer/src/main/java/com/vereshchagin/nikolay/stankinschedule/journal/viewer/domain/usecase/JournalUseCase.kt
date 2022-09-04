package com.vereshchagin.nikolay.stankinschedule.journal.viewer.domain.usecase

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.vereshchagin.nikolay.stankinschedule.journal.core.domain.model.SemesterMarks
import com.vereshchagin.nikolay.stankinschedule.journal.core.domain.model.Student
import com.vereshchagin.nikolay.stankinschedule.journal.core.domain.repository.JournalRepository
import com.vereshchagin.nikolay.stankinschedule.journal.predict.domain.repository.JournalPredictRepository
import com.vereshchagin.nikolay.stankinschedule.journal.viewer.data.source.SemesterMarksSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class JournalUseCase @Inject constructor(
    private val journal: JournalRepository,
    private val predict: JournalPredictRepository,
) {
    fun createPager(student: Student): Pager<String, SemesterMarks> {
        return Pager(
            config = PagingConfig(pageSize = 1),
            initialKey = student.semesters.first(),
            pagingSourceFactory = {
                SemesterMarksSource(
                    journal = journal,
                    semesters = student.semesters,
                    semesterExpireHours = 2
                )
            }
        )
    }

    fun rating(student: Student): Flow<String> = flow {
        val rating = predict.rating(student) { journal.semesterMarks(it) }
        if (rating.isFinite() && rating > 0.0) {
            emit("%.2f".format(rating))
        } else {
            emit("--.--")
        }
    }.flowOn(Dispatchers.Default)

    fun predictRating(student: Student): Flow<String> = flow {
        val rating = predict.predictRating(student) { journal.semesterMarks(it) }
        if (rating.isFinite() && rating > 0.0) {
            emit("%.2f".format(rating))
        } else {
            emit("--.--")
        }
    }.flowOn(Dispatchers.Default)

    fun student(useCache: Boolean): Flow<Student?> = flow {
        val student = journal.student(useCache)
        emit(student)
    }.flowOn(Dispatchers.IO)

    suspend fun signOut() = journal.signOut()

    companion object {
        private const val TAG = "JournalUseCase"
    }
}