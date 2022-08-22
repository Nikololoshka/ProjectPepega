package com.vereshchagin.nikolay.stankinschedule.schedule.viewer.domain.usecase

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.ScheduleInfo
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.ScheduleModel
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.repository.ScheduleStorage
import com.vereshchagin.nikolay.stankinschedule.schedule.viewer.data.source.ScheduleViewerSource
import com.vereshchagin.nikolay.stankinschedule.schedule.viewer.domain.model.ScheduleViewDay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import org.joda.time.LocalDate
import javax.inject.Inject

class ScheduleViewerUseCase @Inject constructor(
    private val storage: ScheduleStorage,
) {
    fun createPager(
        schedule: ScheduleModel,
        initialDay: LocalDate = LocalDate.now(),
    ): Pager<LocalDate, ScheduleViewDay> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                initialLoadSize = 20,
                prefetchDistance = 20 / 2
            ),
            initialKey = LocalDate(2022, 2, 24),
            pagingSourceFactory = {
                ScheduleViewerSource(schedule = schedule)
            }
        )
    }

    fun scheduleInfo(scheduleId: Long): Flow<ScheduleInfo?> = storage.schedule(scheduleId)

    fun scheduleModel(scheduleId: Long): Flow<ScheduleModel?> = storage.scheduleModel(scheduleId)
}