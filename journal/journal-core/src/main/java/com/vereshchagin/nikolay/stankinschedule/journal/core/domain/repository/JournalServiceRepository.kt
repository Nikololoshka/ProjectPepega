package com.vereshchagin.nikolay.stankinschedule.journal.core.domain.repository

import com.vereshchagin.nikolay.stankinschedule.journal.core.data.api.MarkResponse
import com.vereshchagin.nikolay.stankinschedule.journal.core.data.api.SemestersResponse
import com.vereshchagin.nikolay.stankinschedule.journal.core.domain.model.StudentCredentials


interface JournalServiceRepository {

    suspend fun loadSemesters(credentials: StudentCredentials): SemestersResponse

    suspend fun loadMarks(credentials: StudentCredentials, semester: String): List<MarkResponse>
}