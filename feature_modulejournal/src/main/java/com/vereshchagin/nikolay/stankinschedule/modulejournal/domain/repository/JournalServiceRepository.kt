package com.vereshchagin.nikolay.stankinschedule.modulejournal.domain.repository

import com.vereshchagin.nikolay.stankinschedule.modulejournal.data.api.SemestersResponse
import com.vereshchagin.nikolay.stankinschedule.modulejournal.domain.model.Student

interface JournalServiceRepository {

    suspend fun loadSemesters(login: String, password: String) : SemestersResponse

}