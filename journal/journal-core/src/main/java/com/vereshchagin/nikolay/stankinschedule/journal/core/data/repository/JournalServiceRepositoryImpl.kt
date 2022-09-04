package com.vereshchagin.nikolay.stankinschedule.journal.core.data.repository

import com.vereshchagin.nikolay.stankinschedule.journal.core.data.api.MarkResponse
import com.vereshchagin.nikolay.stankinschedule.journal.core.data.api.ModuleJournalAPI
import com.vereshchagin.nikolay.stankinschedule.journal.core.data.api.SemestersResponse
import com.vereshchagin.nikolay.stankinschedule.journal.core.domain.model.StudentCredentials
import com.vereshchagin.nikolay.stankinschedule.journal.core.domain.repository.JournalServiceRepository
import retrofit2.await
import javax.inject.Inject

class JournalServiceRepositoryImpl @Inject constructor(
    private val api: ModuleJournalAPI,
) : JournalServiceRepository {

    override suspend fun loadSemesters(
        credentials: StudentCredentials,
    ): SemestersResponse {
        return api.getSemesters(credentials.login, credentials.password).await()
    }

    override suspend fun loadMarks(
        credentials: StudentCredentials,
        semester: String,
    ): List<MarkResponse> {
        return api.getMarks(credentials.login, credentials.password, semester).await()
    }
}