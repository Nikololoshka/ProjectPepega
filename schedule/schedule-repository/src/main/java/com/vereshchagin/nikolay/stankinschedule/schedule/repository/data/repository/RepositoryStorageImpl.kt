package com.vereshchagin.nikolay.stankinschedule.schedule.repository.data.repository

import androidx.room.RoomDatabase
import androidx.room.withTransaction
import com.vereshchagin.nikolay.stankinschedule.core.cache.CacheContainer
import com.vereshchagin.nikolay.stankinschedule.core.cache.CacheManager
import com.vereshchagin.nikolay.stankinschedule.schedule.repository.data.db.RepositoryDao
import com.vereshchagin.nikolay.stankinschedule.schedule.repository.data.db.RepositoryEntity
import com.vereshchagin.nikolay.stankinschedule.schedule.repository.domain.model.RepositoryDescription
import com.vereshchagin.nikolay.stankinschedule.schedule.repository.domain.repository.RepositoryStorage
import javax.inject.Inject

class RepositoryStorageImpl @Inject constructor(
    private val cache: CacheManager,
    private val db: RoomDatabase,
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

    override suspend fun insertRepositoryEntries(entries: List<RepositoryEntity>) {
        db.withTransaction { dao.insertAll(entries) }
    }

    override suspend fun getRepositoryEntries(category: String): List<RepositoryEntity> {
        return db.withTransaction { dao.getAll(category) }
    }

    override suspend fun deleteAll(category: String) {
        db.withTransaction { dao.deleteAll(category) }
    }

    companion object {
        private const val ROOT = "firebase_storage"
        private const val DESCRIPTION = "description"
    }
}