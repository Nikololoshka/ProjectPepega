package com.vereshchagin.nikolay.stankinschedule.journal.core.domain.usecase

import androidx.paging.PagingSource
import com.vereshchagin.nikolay.stankinschedule.journal.core.domain.model.SemesterMarks
import com.vereshchagin.nikolay.stankinschedule.journal.core.domain.model.Student
import com.vereshchagin.nikolay.stankinschedule.journal.core.domain.repository.JournalPagingRepository
import com.vereshchagin.nikolay.stankinschedule.journal.core.domain.repository.JournalRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class JournalUseCase @Inject constructor(
    private val journal: JournalRepository,
    private val paging: JournalPagingRepository
) {
    fun semesterSource(student: Student): PagingSource<String, SemesterMarks> {
        return paging.semesterSource(
            journal = journal,
            semesters = student.semesters,
            semesterExpireHours = 2
        )
    }

    fun student(useCache: Boolean): Flow<Student?> = flow {
        val student = journal.student(useCache)
        emit(student)
    }.flowOn(Dispatchers.IO)
}