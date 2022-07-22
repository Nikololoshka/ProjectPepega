package com.vereshchagin.nikolay.stankinschedule.modulejournal.domain.repository

import com.vereshchagin.nikolay.stankinschedule.modulejournal.data.api.MarkResponse
import com.vereshchagin.nikolay.stankinschedule.modulejournal.data.api.SemestersResponse
import com.vereshchagin.nikolay.stankinschedule.modulejournal.domain.model.StudentCredentials

interface JournalServiceRepository {

    suspend fun loadSemesters(credentials: StudentCredentials): SemestersResponse

    suspend fun loadMarks(credentials: StudentCredentials, semester: String): List<MarkResponse>
}