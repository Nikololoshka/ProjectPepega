package com.vereshchagin.nikolay.stankinschedule.journal.core.domain.repository

import com.vereshchagin.nikolay.stankinschedule.journal.core.domain.model.SemesterMarks
import com.vereshchagin.nikolay.stankinschedule.journal.core.domain.model.Student
import com.vereshchagin.nikolay.stankinschedule.journal.core.domain.model.StudentCredentials


interface JournalServiceRepository {

    suspend fun loadSemesters(credentials: StudentCredentials): Student

    suspend fun loadMarks(credentials: StudentCredentials, semester: String): SemesterMarks
}