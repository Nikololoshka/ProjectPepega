package com.vereshchagin.nikolay.stankinschedule.modulejournal.domain.repository

import com.vereshchagin.nikolay.stankinschedule.core.cache.CacheContainer
import com.vereshchagin.nikolay.stankinschedule.modulejournal.domain.model.SemesterMarks
import com.vereshchagin.nikolay.stankinschedule.modulejournal.domain.model.Student

interface JournalStorageRepository {

    suspend fun loadStudent(): CacheContainer<Student>?

    suspend fun saveStudent(student: Student)

    suspend fun loadSemester(semester: String): CacheContainer<SemesterMarks>?

    suspend fun saveSemester(semester: String, marks: SemesterMarks)
}