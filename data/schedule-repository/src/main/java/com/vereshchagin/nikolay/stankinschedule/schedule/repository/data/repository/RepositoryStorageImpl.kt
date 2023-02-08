package com.vereshchagin.nikolay.stankinschedule.schedule.repository.data.repository

import androidx.room.withTransaction
import com.vereshchagin.nikolay.stankinschedule.core.data.cache.CacheManager
import com.vereshchagin.nikolay.stankinschedule.core.domain.cache.CacheContainer
import com.vereshchagin.nikolay.stankinschedule.schedule.repository.data.db.RepositoryDao
import com.vereshchagin.nikolay.stankinschedule.schedule.repository.data.db.RepositoryDatabase
import com.vereshchagin.nikolay.stankinschedule.schedule.repository.data.mapper.toEntity
import com.vereshchagin.nikolay.stankinschedule.schedule.repository.data.mapper.toItem
import com.vereshchagin.nikolay.stankinschedule.schedule.repository.domain.model.RepositoryDescription
import com.vereshchagin.nikolay.stankinschedule.schedule.repository.domain.model.RepositoryItem
import com.vereshchagin.nikolay.stankinschedule.schedule.repository.domain.repository.RepositoryStorage
import javax.inject.Inject

class RepositoryStorageImpl @Inject constructor(
    private val cache: CacheManager,
    private val db: RepositoryDatabase,
    private val dao: RepositoryDao,
) : RepositoryStorage {

    init {
        cache.addStartedPath(ROOT)
    }

    override suspend fun loadDescription(): CacheContainer<RepositoryDescription>? {
        return cache.loadFromCache(RepositoryDescription::class.java, DESCRIPTION)
    }

    override suspend fun saveDescription(description: RepositoryDescription) {
        cache.saveToCache(description, DESCRIPTION)
    }

    override suspend fun insertRepositoryEntries(entries: List<RepositoryItem>) {
        db.withTransaction {
            dao.deleteAll()
            dao.insertAll(entries.map { it.toEntity() })
        }
    }

    override suspend fun getRepositoryEntries(category: String): List<RepositoryItem> {
        return db.withTransaction { dao.getAll(category) }.map { it.toItem() }
    }

    override suspend fun clearEntries() {
        db.withTransaction { dao.deleteAll() }
    }

    companion object {
        private const val ROOT = "firebase_storage"
        private const val DESCRIPTION = "description"
    }
}