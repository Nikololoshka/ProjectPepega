package com.vereshchagin.nikolay.stankinschedule.schedule.core.data.repository

import androidx.room.withTransaction
import com.vereshchagin.nikolay.stankinschedule.schedule.core.data.db.ScheduleDao
import com.vereshchagin.nikolay.stankinschedule.schedule.core.data.db.ScheduleDatabase
import com.vereshchagin.nikolay.stankinschedule.schedule.core.data.mapper.toEntity
import com.vereshchagin.nikolay.stankinschedule.schedule.core.data.mapper.toInfo
import com.vereshchagin.nikolay.stankinschedule.schedule.core.data.mapper.toPairModel
import com.vereshchagin.nikolay.stankinschedule.schedule.core.data.mapper.toScheduleModel
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.PairModel
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.ScheduleInfo
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.ScheduleModel
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.repository.ScheduleStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ScheduleStorageImpl @Inject constructor(
    private val db: ScheduleDatabase,
    private val dao: ScheduleDao,
) : ScheduleStorage {

    override fun schedules(): Flow<List<ScheduleInfo>> {
        return dao.getAllSchedules().map { data -> data.map { it.toInfo() } }
    }

    override fun schedule(scheduleId: Long): Flow<ScheduleInfo?> {
        return dao.getScheduleEntity(scheduleId).map { it?.toInfo() }
    }

    override fun scheduleModel(scheduleId: Long): Flow<ScheduleModel?> {
        return dao.getScheduleWithPairs(scheduleId).map { it?.toScheduleModel() }
    }

    override fun schedulePair(pairId: Long): Flow<PairModel?> {
        return dao.getPairEntity(pairId).map { it?.toPairModel() }
    }

    override suspend fun saveSchedule(model: ScheduleModel, replaceExist: Boolean) {
        db.withTransaction {
            if (replaceExist) {
                val prevItem = dao.getScheduleEntity(model.info.scheduleName)
                if (prevItem != null) {
                    dao.deleteSchedulePairs(prevItem.id)

                    val pairEntities = model.map { pair -> pair.toEntity(prevItem.id) }
                    dao.insertPairs(pairEntities)

                    return@withTransaction
                }
            }

            val lastPosition = dao.getScheduleCount()
            val scheduleEntity = model.toEntity(position = lastPosition)
            val scheduleId = dao.insertScheduleEntity(scheduleEntity)

            val pairEntities = model.map { pair -> pair.toEntity(scheduleId) }
            dao.insertPairs(pairEntities)
        }
    }

    override suspend fun isScheduleExist(scheduleName: String): Boolean {
        return dao.isScheduleExist(scheduleName)
    }

    override suspend fun updateSchedules(schedules: List<ScheduleInfo>) {
        db.withTransaction {
            dao.updateScheduleItems(schedules.map { it.toEntity() })
        }
    }

    override suspend fun removeSchedule(id: Long) {
        db.withTransaction {
            dao.deleteSchedule(id)
        }
    }

    override suspend fun removeSchedules(schedules: List<ScheduleInfo>) {
        db.withTransaction {
            schedules.forEach { schedule -> dao.deleteSchedule(schedule.id) }
        }
    }

    override suspend fun removeSchedulePair(pair: PairModel) {
        db.withTransaction {
            dao.deletePairEntity(pair.info.id)
        }
    }

    override suspend fun renameSchedule(id: Long, scheduleName: String) {
        val entity = dao.getScheduleEntity(id).firstOrNull()
        db.withTransaction {
            if (entity != null) {
                val newEntity = entity.copy(scheduleName = scheduleName)
                dao.updateScheduleItem(newEntity)
            }
        }
    }
}
