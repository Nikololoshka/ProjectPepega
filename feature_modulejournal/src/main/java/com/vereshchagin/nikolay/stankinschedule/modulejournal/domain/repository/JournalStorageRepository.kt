package com.vereshchagin.nikolay.stankinschedule.modulejournal.domain.repository

import com.vereshchagin.nikolay.stankinschedule.modulejournal.domain.model.SemesterMarks
import com.vereshchagin.nikolay.stankinschedule.modulejournal.domain.model.Student

interface JournalStorageRepository {

    suspend fun saveStudent(student: Student)

    suspend fun loadStudent(): Student?

    suspend fun loadSemester(semester: String): SemesterMarks?

    suspend fun saveSemester(semester: String, marks: SemesterMarks)
}