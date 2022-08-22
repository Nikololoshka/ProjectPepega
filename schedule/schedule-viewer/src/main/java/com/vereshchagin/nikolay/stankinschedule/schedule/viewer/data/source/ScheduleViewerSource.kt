package com.vereshchagin.nikolay.stankinschedule.schedule.viewer.data.source

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.ScheduleModel
import com.vereshchagin.nikolay.stankinschedule.schedule.viewer.domain.model.ScheduleViewDay
import com.vereshchagin.nikolay.stankinschedule.schedule.viewer.BuildConfig
import org.joda.time.LocalDate

class ScheduleViewerSource(
    private val schedule: ScheduleModel,
) : PagingSource<LocalDate, ScheduleViewDay>() {

    private val startDate = schedule.startDate()
    private val endDate = schedule.endDate()

    override fun getRefreshKey(state: PagingState<LocalDate, ScheduleViewDay>): LocalDate? {
        if (BuildConfig.DEBUG) {
            Log.d("ScheduleViewSourceLog", "getRefreshKey: $state")
        }

        val position = state.anchorPosition
        if (position != null) {
            state.pages.getOrNull(position)
        }
        return null
    }

    override suspend fun load(
        params: LoadParams<LocalDate>
    ): LoadResult<LocalDate, ScheduleViewDay> {
        val date = params.key ?: LocalDate.now()
        val loadSize = params.loadSize

        // нет пар в расписании
        if (startDate == null && endDate == null) {
            return LoadResult.Page(listOf(), null, null)
        }

        val nextDay = nextDay(date, loadSize)
        val prevDay = prevDay(date, loadSize)

        if (BuildConfig.DEBUG) {
            Log.d("ScheduleViewSourceLog",
                "Load view data: " +
                        "${prevDay?.toString("dd.MM.yyyy")} " +
                        "<- ${date.toString("dd.MM.yyyy")} -> " +
                        "${nextDay?.toString("dd.MM.yyyy")}. Total: $loadSize"
            )
        }

        return LoadResult.Page(
            loadDays(date, nextDay),
            prevDay,
            nextDay
        )
    }

    /**
     * Загружает необходимое количество дней в расписание.
     */
    private fun loadDays(from: LocalDate, to: LocalDate?): List<ScheduleViewDay> {
        var begin = from
        val end = to ?: endDate!!

        if (BuildConfig.DEBUG) {
            Log.d(
                "ScheduleViewSourceLog",
                "load: ${begin.toString("dd.MM.yyyy")} " +
                        "<--> ${end.toString("dd.MM.yyyy")}"
            )
        }

        val result = ArrayList<ScheduleViewDay>()
        while (begin < end) {
            result.add(
                ScheduleViewDay(
                    schedule.pairsByDate(begin),
                    begin
                )
            )
            begin = begin.plusDays(1)
        }

        if (BuildConfig.DEBUG) {
            Log.d("ScheduleViewSourceLog", "loaded = ${result.size}")
        }

        return result
    }

    /**
     * Вычисляет следующий день для загрузки данных.
     */
    private fun nextDay(currentDate: LocalDate, pageSize: Int): LocalDate? {
        return currentDate.plusDays(pageSize)
    }

    /**
     * Вычисляет предыдущий день для загрузки данных.
     */
    private fun prevDay(currentDate: LocalDate, pageSize: Int): LocalDate? {
        return currentDate.minusDays(pageSize)
    }
}