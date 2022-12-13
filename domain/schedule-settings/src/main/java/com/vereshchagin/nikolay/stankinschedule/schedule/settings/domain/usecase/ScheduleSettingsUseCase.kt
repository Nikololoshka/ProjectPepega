package com.vereshchagin.nikolay.stankinschedule.schedule.settings.domain.usecase

import com.vereshchagin.nikolay.stankinschedule.schedule.settings.domain.model.PairColorGroup
import com.vereshchagin.nikolay.stankinschedule.schedule.settings.domain.repository.SchedulePreference
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ScheduleSettingsUseCase @Inject constructor(
    private val preference: SchedulePreference,
) {
    suspend fun setFavorite(id: Long) = preference.setFavorite(id)

    fun favorite(): Flow<Long> = preference.favorite()

    fun isVerticalViewer(): Flow<Boolean> = preference.isVerticalViewer()

    fun pairColorGroup(): Flow<PairColorGroup> = preference.scheduleColorGroup()
}