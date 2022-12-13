package com.vereshchagin.nikolay.stankinschedule.schedule.viewer.domain.usecase

import androidx.paging.PagingSource
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.ScheduleModel
import com.vereshchagin.nikolay.stankinschedule.schedule.viewer.domain.model.ScheduleViewDay
import com.vereshchagin.nikolay.stankinschedule.schedule.viewer.domain.repository.ScheduleViewerRepository
import org.joda.time.LocalDate
import javax.inject.Inject

class ScheduleViewerUseCase @Inject constructor(
    private val repository: ScheduleViewerRepository
) {
    fun scheduleSource(model: ScheduleModel): PagingSource<LocalDate, ScheduleViewDay> =
        repository.scheduleSource(model)

    fun scheduleViewDays(
        model: ScheduleModel,
        from: LocalDate,
        to: LocalDate
    ): List<ScheduleViewDay> {
        val days = mutableListOf<ScheduleViewDay>()

        var it = from
        while (it.isBefore(to)) {

            days += ScheduleViewDay(
                day = it,
                pairs = model.pairsByDate(it).map { repository.convertToViewPair(it) }
            )

            it = it.plusDays(1)
        }

        return days
    }
}