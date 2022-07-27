package com.vereshchagin.nikolay.stankinschedule.modulejournal.data.source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.vereshchagin.nikolay.stankinschedule.modulejournal.domain.model.SemesterMarks
import com.vereshchagin.nikolay.stankinschedule.modulejournal.domain.repository.JournalRepository

class SemesterMarksSource(
    private val journal: JournalRepository,
    private val semesters: List<String>,
    private val semesterExpireHours: Int,
) : PagingSource<String, SemesterMarks>() {

    override suspend fun load(
        params: LoadParams<String>,
    ): LoadResult<String, SemesterMarks> {
        return try {
            val semester = params.key ?: semesters.first()
            val index = semesters.indexOf(semester)
            val marks = journal.semesterMarks(semester, semesterExpireHours)

            LoadResult.Page(
                data = listOf(marks),
                prevKey = semesters.getOrNull(index - 1),
                nextKey = semesters.getOrNull(index + 1),
                itemsBefore = index,
                itemsAfter = (semesters.size - 1) - index
            )
        } catch (t: Throwable) {
            LoadResult.Error(t)
        }
    }

    override fun getRefreshKey(state: PagingState<String, SemesterMarks>): String? {
        return state.anchorPosition?.let { anchorPosition -> semesters[anchorPosition] }
    }
}