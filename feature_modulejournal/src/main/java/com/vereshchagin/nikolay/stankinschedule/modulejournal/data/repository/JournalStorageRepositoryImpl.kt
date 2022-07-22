package com.vereshchagin.nikolay.stankinschedule.modulejournal.data.repository

import com.vereshchagin.nikolay.stankinschedule.core.cache.CacheContainer
import com.vereshchagin.nikolay.stankinschedule.core.cache.CacheManager
import com.vereshchagin.nikolay.stankinschedule.modulejournal.domain.model.MarkType
import com.vereshchagin.nikolay.stankinschedule.modulejournal.domain.model.SemesterMarks
import com.vereshchagin.nikolay.stankinschedule.modulejournal.domain.model.Student
import com.vereshchagin.nikolay.stankinschedule.modulejournal.domain.repository.JournalStorageRepository
import com.vereshchagin.nikolay.stankinschedule.modulejournal.util.MarkTypeTypeConverter
import javax.inject.Inject

class JournalStorageRepositoryImpl @Inject constructor(
    private val cache: CacheManager
) : JournalStorageRepository {

    init {
        cache.addStartedPath("module_journal")
        cache.configurateParser {
            registerTypeAdapter(MarkType::class.java, MarkTypeTypeConverter())
        }
    }

    override suspend fun saveStudent(student: Student) {
        cache.saveToCache(student, "student")
    }

    override suspend fun loadStudent(): CacheContainer<Student>? {
        return cache.loadFromCache(Student::class.java, "student")
    }

    override suspend fun saveSemester(semester: String, marks: SemesterMarks) {
        cache.saveToCache(marks, semester)
    }

    override suspend fun loadSemester(semester: String): CacheContainer<SemesterMarks>? {
        return cache.loadFromCache(SemesterMarks::class.java, semester)
    }
}