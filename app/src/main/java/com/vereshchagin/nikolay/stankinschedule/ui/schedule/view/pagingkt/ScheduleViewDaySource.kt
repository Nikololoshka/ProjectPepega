package com.vereshchagin.nikolay.stankinschedule.ui.schedule.view.pagingkt

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.vereshchagin.nikolay.stankinschedule.BuildConfig
import com.vereshchagin.nikolay.stankinschedule.model.schedule.Schedule
import org.joda.time.LocalDate

/**
 * Источник данных (дней) для простомтра расписания.
 */
class ScheduleViewDaySource(
    private val schedule: Schedule,
    private val limit: Boolean
) : PagingSource<LocalDate, ScheduleViewDay>() {

    private val startDate = schedule.startDate()
    private val endDate = schedule.endDate()

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

        return LoadResult.Page(
            loadDays(date, nextDay),
            prevDay,
            nextDay
        )
    }

    @ExperimentalPagingApi
    override fun getRefreshKey(state: PagingState<LocalDate, ScheduleViewDay>): LocalDate? {
        return super.getRefreshKey(state)
    }

    /**
     * Загружает необходимое количество дней в расписание.
     */
    private fun loadDays(from: LocalDate, to: LocalDate?): List<ScheduleViewDay> {
        var begin = if (limit) {
            if (from < startDate) startDate!! else from
        } else {
            from
        }

        val end: LocalDate = if (limit) {
            if (to == null) {
                endDate!!
            } else {
                if (to > endDate) endDate?.plusDays(1)!! else to
            }
        } else {
            to ?: endDate!!
        }

        if (BuildConfig.DEBUG) {
            Log.d(
                "ScheduleViewSource",
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

        return result
    }

    /**
     * Вычисляет следующий день для загрузки данных.
     */
    private fun nextDay(currentDate: LocalDate, pageSize: Int): LocalDate? {
        if (limit) {
            if (currentDate > endDate) {
                return null
            }
        }

        return currentDate.plusDays(pageSize)
    }

    /**
     * Вычисляет предыдущий день для загрузки данных.
     */
    private fun prevDay(currentDate: LocalDate, pageSize: Int): LocalDate? {
        if (limit) {
            if (currentDate < startDate) {
                return null
            }
        }

        return currentDate.minusDays(pageSize)
    }
}