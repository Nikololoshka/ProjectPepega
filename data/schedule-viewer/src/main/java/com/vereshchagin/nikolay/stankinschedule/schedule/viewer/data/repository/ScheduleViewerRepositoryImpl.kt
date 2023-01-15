package com.vereshchagin.nikolay.stankinschedule.schedule.viewer.data.repository

import androidx.paging.PagingSource
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.PairModel
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.ScheduleModel
import com.vereshchagin.nikolay.stankinschedule.schedule.viewer.data.mapper.toViewPair
import com.vereshchagin.nikolay.stankinschedule.schedule.viewer.data.source.ScheduleViewerSource
import com.vereshchagin.nikolay.stankinschedule.schedule.viewer.domain.model.ScheduleViewDay
import com.vereshchagin.nikolay.stankinschedule.schedule.viewer.domain.model.ScheduleViewPair
import com.vereshchagin.nikolay.stankinschedule.schedule.viewer.domain.repository.ScheduleViewerRepository
import org.joda.time.LocalDate
import javax.inject.Inject

class ScheduleViewerRepositoryImpl @Inject constructor() : ScheduleViewerRepository {

    override fun scheduleSource(schedule: ScheduleModel): PagingSource<LocalDate, ScheduleViewDay> {
        return ScheduleViewerSource(schedule)
    }

    override fun convertToViewPair(pair: PairModel): ScheduleViewPair {
        return pair.toViewPair()
    }
}