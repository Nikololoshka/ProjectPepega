package com.vereshchagin.nikolay.stankinschedule.schedule.repository.domain.repository

import com.vereshchagin.nikolay.stankinschedule.core.cache.CacheContainer
import com.vereshchagin.nikolay.stankinschedule.schedule.repository.data.db.RepositoryEntity
import com.vereshchagin.nikolay.stankinschedule.schedule.repository.domain.model.RepositoryDescription

interface RepositoryStorage {

    suspend fun loadDescription(): CacheContainer<RepositoryDescription>?

    suspend fun saveDescription(description: RepositoryDescription)

    suspend fun insertRepositoryEntries(entries: List<RepositoryEntity>)

    suspend fun getRepositoryEntries(category: String): List<RepositoryEntity>
}