package com.vereshchagin.nikolay.stankinschedule.schedule.data.repository

import androidx.room.RoomDatabase
import androidx.room.withTransaction
import com.vereshchagin.nikolay.stankinschedule.schedule.data.db.ScheduleDao
import com.vereshchagin.nikolay.stankinschedule.schedule.data.mapper.toEntity
import com.vereshchagin.nikolay.stankinschedule.schedule.data.mapper.toInfo
import com.vereshchagin.nikolay.stankinschedule.schedule.domain.model.ScheduleInfo
import com.vereshchagin.nikolay.stankinschedule.schedule.domain.model.ScheduleModel
import com.vereshchagin.nikolay.stankinschedule.schedule.domain.repository.ScheduleStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ScheduleStorageImpl @Inject constructor(
    private val db: RoomDatabase,
    private val dao: ScheduleDao,
) : ScheduleStorage {

    override fun schedules(): Flow<List<ScheduleInfo>> {
        return dao.getAllSchedules().map { data -> data.map { it.toInfo() } }
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

    override suspend fun removeSchedules(schedules: List<ScheduleInfo>) {
        db.withTransaction {
            schedules.forEach { schedule -> dao.deleteSchedule(schedule.id) }
        }
    }
}
