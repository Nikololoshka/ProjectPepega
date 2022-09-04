package com.vereshchagin.nikolay.stankinschedule.schedule.repository.domain.usecase

import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.repository.ScheduleStorage
import com.vereshchagin.nikolay.stankinschedule.schedule.repository.data.mapper.toDescription
import com.vereshchagin.nikolay.stankinschedule.schedule.repository.data.mapper.toEntity
import com.vereshchagin.nikolay.stankinschedule.schedule.repository.data.mapper.toItem
import com.vereshchagin.nikolay.stankinschedule.schedule.repository.domain.model.RepositoryDescription
import com.vereshchagin.nikolay.stankinschedule.schedule.repository.domain.model.RepositoryItem
import com.vereshchagin.nikolay.stankinschedule.schedule.repository.domain.repository.RepositoryStorage
import com.vereshchagin.nikolay.stankinschedule.schedule.repository.domain.repository.ScheduleRemoteService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


class RepositoryUseCase @Inject constructor(
    private val service: ScheduleRemoteService,
    private val repositoryStorage: RepositoryStorage,
    private val scheduleStorage: ScheduleStorage,
) {

    fun repositoryDescription(
        useCache: Boolean = true,
    ): Flow<RepositoryDescription> = flow {
        val cache = repositoryStorage.loadDescription()
        if (cache != null && useCache) {
            emit(cache.data)
            return@flow
        }

        try {
            val response = service.description()
            val description = response.toDescription()

            repositoryStorage.saveDescription(description)
            emit(description)

        } catch (e: Exception) {
            if (cache != null) {
                emit(cache.data)
                return@flow
            }

            throw e
        }
    }

    fun repositoryItems(
        category: String,
        useCache: Boolean = true,
    ): Flow<List<RepositoryItem>> = flow {
        val cache = repositoryStorage.getRepositoryEntries(category)
        if (cache.isNotEmpty() && useCache) {
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