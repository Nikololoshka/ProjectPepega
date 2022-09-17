package com.vereshchagin.nikolay.stankinschedule.schedule.viewer.data.source

import android.net.Uri
import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.PairModel
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.ScheduleModel
import com.vereshchagin.nikolay.stankinschedule.schedule.viewer.BuildConfig
import com.vereshchagin.nikolay.stankinschedule.schedule.viewer.domain.model.ScheduleViewDay
import com.vereshchagin.nikolay.stankinschedule.schedule.viewer.domain.model.ScheduleViewPair
import com.vereshchagin.nikolay.stankinschedule.schedule.viewer.domain.model.ViewContent
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
            Log.d(
                "ScheduleViewSourceLog",
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
                    pairs = schedule.pairsByDate(begin).map { it.toViewPair() },
                    day = begin
                )
            )
            begin = begin.plusDays(1)
        }

        if (BuildConfig.DEBUG) {
            Log.d("ScheduleViewSourceLog", "loaded = ${result.size}")
        }

        return result
    }

    private fun PairModel.toViewPair(): ScheduleViewPair {
        return ScheduleViewPair(
            id = info.id,
            title = title,
            lecturer = lecturer,
            classroom = classroomViewContent(classroom),
            subgroup = subgroup,
            type = type,
            startTime = time.startString(),
            endTime = time.endString()
        )
    }

    private fun classroomViewContent(classroom: String): ViewContent {
        val uri = Uri.parse(classroom)
        val host = uri.host ?: return ViewContent.TextContent(classroom)

        return ViewContent.LinkContent(
            name = host
                .removePrefix(prefix = "www.")
                .substringBeforeLast(delimiter = '.'),
            link = classroom
        )
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