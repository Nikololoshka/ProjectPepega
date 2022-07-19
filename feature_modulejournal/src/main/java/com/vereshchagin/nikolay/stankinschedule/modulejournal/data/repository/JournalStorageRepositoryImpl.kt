package com.vereshchagin.nikolay.stankinschedule.modulejournal.data.repository

import com.vereshchagin.nikolay.stankinschedule.core.ui.CacheManager
import com.vereshchagin.nikolay.stankinschedule.modulejournal.domain.model.Student
import com.vereshchagin.nikolay.stankinschedule.modulejournal.domain.repository.JournalStorageRepository
import javax.inject.Inject

class JournalStorageRepositoryImpl @Inject constructor(
    private val cache: CacheManager
) : JournalStorageRepository {

    init {
        cache.addStartedPath("module_journal")
    }

    override suspend fun saveStudent(student: Student) {
        cache.saveToCache(student, "student")
    }

    override suspend fun loadStudent(): Student? {
        return cache.loadFromCache(Student::class.java, "student")
    }
}