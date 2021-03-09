package com.vereshchagin.nikolay.stankinschedule.ui.schedule.editor.view.paging

import androidx.paging.PagingSource
import com.vereshchagin.nikolay.stankinschedule.model.schedule.Schedule
import com.vereshchagin.nikolay.stankinschedule.model.schedule.db.PairItem
import com.vereshchagin.nikolay.stankinschedule.model.schedule.editor.ScheduleEditorDiscipline
import com.vereshchagin.nikolay.stankinschedule.model.schedule.pair.Type

/**
 *
 */
class ScheduleDisciplineSource(
    private val schedule: Schedule,
    private val disciplines: List<String>,
) : PagingSource<String, ScheduleEditorDiscipline>() {

    override suspend fun load(
        params: LoadParams<String>,
    ): LoadResult<String, ScheduleEditorDiscipline> {
        if (disciplines.isEmpty()) {
            return LoadResult.Page(emptyList(), null, null)
        }

        var index = disciplines.indexOf(params.key)
        if (index == -1) {
            index = 0
        }

        val discipline = disciplines[index]
        val lecturers = arrayListOf<PairItem>()
        val seminars = arrayListOf<PairItem>()
        val labs = arrayListOf<PairItem>()

        val pairs = schedule.pairsByDiscipline(discipline)
        for (pair in pairs) {
            when (pair.type) {
                Type.LECTURE -> lecturers.add(pair)
                Type.SEMINAR -> seminars.add(pair)
                Type.LABORATORY -> labs.add(pair)
            }
        }

        return LoadResult.Page(
            listOf(ScheduleEditorDiscipline(discipline, lecturers, seminars, labs)),
            disciplines.getOrNull(index - 1),
            disciplines.getOrNull(index + 1)
        )
    }
}