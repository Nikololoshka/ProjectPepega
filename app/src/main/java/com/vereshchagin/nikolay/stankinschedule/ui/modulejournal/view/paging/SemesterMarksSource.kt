package com.vereshchagin.nikolay.stankinschedule.ui.modulejournal.view.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.vereshchagin.nikolay.stankinschedule.model.modulejournal.SemesterMarks
import com.vereshchagin.nikolay.stankinschedule.repository.ModuleJournalRepository

/**
 * Источник данных семестров с оценками.
 */
class SemesterMarksSource(
    private val repository: ModuleJournalRepository,
    private val semesters: List<String>,
    private val useCache: Boolean
) : PagingSource<String, SemesterMarks>() {

    override suspend fun load(params: LoadParams<String>): LoadResult<String, SemesterMarks> {
        return try {

            val semester = params.key ?: semesters.last()
            val index = semesters.indexOf(semester)
            val marks = repository.loadSemesterMarks(
                semester, !useCache, index == semesters.size - 1
            )

            // индексы отражены наоборот, т.к. pager справа - налево
            LoadResult.Page(
                listOf(marks),
                semesters.getOrNull(index + 1),
                semesters.getOrNull(index - 1),
                semesters.size - 1 - index,
                index

            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<String, SemesterMarks>): String? {
        return state.anchorPosition?.let { anchorPosition ->
            semesters[anchorPosition]
        }
    }
}