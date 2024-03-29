package com.vereshchagin.nikolay.stankinschedule.journal.core.domain.repository

import com.vereshchagin.nikolay.stankinschedule.core.domain.cache.CacheContainer
import com.vereshchagin.nikolay.stankinschedule.journal.core.domain.model.SemesterMarks
import com.vereshchagin.nikolay.stankinschedule.journal.core.domain.model.Student

interface JournalStorageRepository {

    suspend fun loadStudent(): CacheContainer<Student>?

    suspend fun saveStudent(student: Student)

    suspend fun loadSemester(semester: String): CacheContainer<SemesterMarks>?

    suspend fun saveSemester(semester: String, marks: SemesterMarks)

    suspend fun clear()
}