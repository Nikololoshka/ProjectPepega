package com.vereshchagin.nikolay.stankinschedule.modulejournal.domain.usecase

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.vereshchagin.nikolay.stankinschedule.modulejournal.data.source.SemesterMarksSource
import com.vereshchagin.nikolay.stankinschedule.modulejournal.domain.model.SemesterMarks
import com.vereshchagin.nikolay.stankinschedule.modulejournal.domain.model.Student
import com.vereshchagin.nikolay.stankinschedule.modulejournal.domain.repository.JournalPredictRepository
import com.vereshchagin.nikolay.stankinschedule.modulejournal.domain.repository.JournalRepository
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


    companion object {
        private const val TAG = "JournalUseCase"
    }
}