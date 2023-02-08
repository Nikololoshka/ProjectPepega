package com.vereshchagin.nikolay.stankinschedule.schedule.repository.domain.repository

import com.vereshchagin.nikolay.stankinschedule.core.domain.cache.CacheContainer
import com.vereshchagin.nikolay.stankinschedule.schedule.repository.domain.model.RepositoryDescription
import com.vereshchagin.nikolay.stankinschedule.schedule.repository.domain.model.RepositoryItem

interface RepositoryStorage {

    suspend fun loadDescription(): CacheContainer<RepositoryDescription>?

    suspend fun saveDescription(description: RepositoryDescription)

    suspend fun insertRepositoryEntries(entries: List<RepositoryItem>)

    suspend fun getRepositoryEntries(category: String): List<RepositoryItem>
    suspend fun clearEntries()
}