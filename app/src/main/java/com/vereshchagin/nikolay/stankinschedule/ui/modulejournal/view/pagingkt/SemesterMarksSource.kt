package com.vereshchagin.nikolay.stankinschedule.ui.modulejournal.view.pagingkt

import androidx.paging.PagingSource
import com.vereshchagin.nikolay.stankinschedule.model.modulejournal.SemesterMarks

/**
 *
 */
class SemesterMarksSource : PagingSource<String, SemesterMarks>() {

    override suspend fun load(params: LoadParams<String>): LoadResult<String, SemesterMarks> {
        TODO("Not yet implemented")
    }
}