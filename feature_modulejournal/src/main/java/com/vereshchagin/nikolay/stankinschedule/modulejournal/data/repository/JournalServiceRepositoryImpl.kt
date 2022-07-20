package com.vereshchagin.nikolay.stankinschedule.modulejournal.data.repository

import com.vereshchagin.nikolay.stankinschedule.modulejournal.data.api.ModuleJournalAPI
import com.vereshchagin.nikolay.stankinschedule.modulejournal.data.api.SemestersResponse
import com.vereshchagin.nikolay.stankinschedule.modulejournal.domain.repository.JournalServiceRepository
import retrofit2.await
import javax.inject.Inject

class JournalServiceRepositoryImpl @Inject constructor(
    private val api: ModuleJournalAPI
) : JournalServiceRepository {

    override suspend fun loadSemesters(login: String, password: String): SemestersResponse {
        return api.getSemesters(login, password).await()
    }
}