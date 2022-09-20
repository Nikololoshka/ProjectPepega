package com.vereshchagin.nikolay.stankinschedule.schedule.viewer.domain.usecase

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.ScheduleInfo
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.ScheduleModel
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.repository.ScheduleStorage
import com.vereshchagin.nikolay.stankinschedule.schedule.viewer.data.source.ScheduleViewerSource
import com.vereshchagin.nikolay.stankinschedule.schedule.viewer.domain.model.ScheduleViewDay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
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
                pageSize = 30,
                initialLoadSize = 30,
                prefetchDistance = 10,
                enablePlaceholders = false
            ),
            initialKey = initialDay,
            pagingSourceFactory = {
                ScheduleViewerSource(schedule = schedule)
            }
        )
    }

    fun scheduleInfo(scheduleId: Long): Flow<ScheduleInfo?> = storage.schedule(scheduleId)

    fun scheduleModel(scheduleId: Long): Flow<ScheduleModel?> = storage.scheduleModel(scheduleId)

    suspend fun removeSchedule(scheduleId: Long) = withContext(Dispatchers.IO) {
        storage.removeSchedule(scheduleId)
    }

    fun renameSchedule(scheduleId: Long, scheduleName: String): Flow<Boolean> = flow {
        if (storage.isScheduleExist(scheduleName)) {
            emit(false)
            return@flow
        }

        storage.renameSchedule(scheduleId, scheduleName)
        emit(true)

    }.flowOn(Dispatchers.IO)
}