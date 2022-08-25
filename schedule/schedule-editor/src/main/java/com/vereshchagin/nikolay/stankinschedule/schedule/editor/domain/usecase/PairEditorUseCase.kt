package com.vereshchagin.nikolay.stankinschedule.schedule.editor.domain.usecase

import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.PairModel
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.repository.ScheduleStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class PairEditorUseCase @Inject constructor(
    private val storage: ScheduleStorage,
) {

    suspend fun pair(pairId: Long): PairModel? {
        return storage.schedulePair(pairId).first()
    }

    suspend fun deletePair(pair: PairModel) {
        storage.removeSchedulePair(pair)
    }

    suspend fun changePair(scheduleId: Long, pair: PairModel?, newPair: PairModel) {
        val schedule = storage.scheduleModel(scheduleId).first()
            ?: throw IllegalArgumentException("Schedule $scheduleId not exist")

        schedule.changePair(pair, newPair)
        storage.saveSchedule(schedule, replaceExist = true)
    }

}