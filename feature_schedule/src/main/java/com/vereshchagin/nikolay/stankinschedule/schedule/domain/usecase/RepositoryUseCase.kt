package com.vereshchagin.nikolay.stankinschedule.schedule.domain.usecase

import com.vereshchagin.nikolay.stankinschedule.schedule.data.mapper.toDescription
import com.vereshchagin.nikolay.stankinschedule.schedule.data.mapper.toEntity
import com.vereshchagin.nikolay.stankinschedule.schedule.data.mapper.toItem
import com.vereshchagin.nikolay.stankinschedule.schedule.domain.model.RepositoryDescription
import com.vereshchagin.nikolay.stankinschedule.schedule.domain.model.RepositoryItem
import com.vereshchagin.nikolay.stankinschedule.schedule.domain.repository.RepositoryStorage
import com.vereshchagin.nikolay.stankinschedule.schedule.domain.repository.ScheduleRemoteService
import com.vereshchagin.nikolay.stankinschedule.schedule.domain.repository.ScheduleStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


class RepositoryUseCase @Inject constructor(
    private val service: ScheduleRemoteService,
    private val repositoryStorage: RepositoryStorage,
    private val scheduleStorage: ScheduleStorage,
) {

    fun repositoryDescription(): Flow<RepositoryDescription> = flow {
        val cache = repositoryStorage.loadDescription()
        if (cache != null) {
            emit(cache.data)
            return@flow
        }

        val response = service.description()
        val description = response.toDescription()

        repositoryStorage.saveDescription(description)
        emit(description)
    }

    fun repositoryItems(category: String): Flow<List<RepositoryItem>> = flow {
        val cache = repositoryStorage.getRepositoryEntries(category)
        if (cache.isNotEmpty()) {
            val items = cache.map { it.toItem() }
            emit(items)
            return@flow
        }

        val response = service.category(category)
        val items = response.map { it.toItem(category) }
        val entities = items.map { it.toEntity() }

        repositoryStorage.insertRepositoryEntries(entities)
        emit(items)
    }

    suspend fun isScheduleExist(scheduleName: String): Boolean {
        return scheduleStorage.isScheduleExist(scheduleName)
    }
}