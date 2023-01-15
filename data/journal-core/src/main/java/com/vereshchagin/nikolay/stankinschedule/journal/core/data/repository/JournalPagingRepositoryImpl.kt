package com.vereshchagin.nikolay.stankinschedule.journal.core.data.repository

import androidx.paging.PagingSource
import com.vereshchagin.nikolay.stankinschedule.journal.core.data.source.SemesterMarksSource
import com.vereshchagin.nikolay.stankinschedule.journal.core.domain.model.SemesterMarks
import com.vereshchagin.nikolay.stankinschedule.journal.core.domain.repository.JournalPagingRepository
import com.vereshchagin.nikolay.stankinschedule.journal.core.domain.repository.JournalRepository
import javax.inject.Inject

class JournalPagingRepositoryImpl @Inject constructor() : JournalPagingRepository {

    override fun semesterSource(
        journal: JournalRepository,
        semesters: List<String>,
        semesterExpireHours: Int
    ): PagingSource<String, SemesterMarks> {
        return SemesterMarksSource(
            journal = journal,
            semesters = semesters,
            semesterExpireHours = semesterExpireHours
        )
    }
}