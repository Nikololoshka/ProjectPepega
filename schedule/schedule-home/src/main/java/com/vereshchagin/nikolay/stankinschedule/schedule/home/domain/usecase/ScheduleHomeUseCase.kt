package com.vereshchagin.nikolay.stankinschedule.schedule.home.domain.usecase

import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.repository.ScheduleStorage
import com.vereshchagin.nikolay.stankinschedule.schedule.core.ui.data.ScheduleViewDay
import com.vereshchagin.nikolay.stankinschedule.schedule.core.ui.data.toViewPair
import com.vereshchagin.nikolay.stankinschedule.schedule.home.domain.model.ScheduleHomeInfo
import com.vereshchagin.nikolay.stankinschedule.schedule.settings.domain.model.PairColorGroup
import com.vereshchagin.nikolay.stankinschedule.schedule.settings.domain.repository.SchedulePreference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import org.joda.time.LocalDate
import javax.inject.Inject

class ScheduleHomeUseCase @Inject constructor(
    private val storage: ScheduleStorage,
    private val preference: SchedulePreference
) {

    fun pairColorGroup(): Flow<PairColorGroup> = preference.scheduleColorGroup()

    fun favoriteSchedule(
        from: LocalDate,
        to: LocalDate
    ): Flow<ScheduleHomeInfo?> = preference.favorite()
        .map { id ->
            val model = storage.scheduleModel(id).firstOrNull() ?: return@map null

            val days = mutableListOf<ScheduleViewDay>()

            var it = from
            while (it.isBefore(to)) {

                days += ScheduleViewDay(
                    day = it,
                    pairs = model.pairsByDate(it).map { it.toViewPair() }
                )

                it = it.plusDays(1)
            }

            ScheduleHomeInfo(
                scheduleName = model.info.scheduleName,
                scheduleId = model.info.id,
                days = days
            )
        }
        .flowOn(Dispatchers.IO)
}