package com.vereshchagin.nikolay.stankinschedule.modulejournal.domain.repository

import com.vereshchagin.nikolay.stankinschedule.modulejournal.data.api.MarkResponse
import com.vereshchagin.nikolay.stankinschedule.modulejournal.data.api.SemestersResponse

interface JournalServiceRepository {

    suspend fun loadSemesters(login: String, password: String) : SemestersResponse

    suspend fun loadMarks(login: String, password: String, semester: String): List<MarkResponse>
}