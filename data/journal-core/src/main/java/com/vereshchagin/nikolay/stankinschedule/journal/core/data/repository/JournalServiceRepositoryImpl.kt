package com.vereshchagin.nikolay.stankinschedule.journal.core.data.repository

import com.vereshchagin.nikolay.stankinschedule.journal.core.data.api.ModuleJournalAPI
import com.vereshchagin.nikolay.stankinschedule.journal.core.data.mapper.toSemesterMarks
import com.vereshchagin.nikolay.stankinschedule.journal.core.data.mapper.toStudent
import com.vereshchagin.nikolay.stankinschedule.journal.core.domain.model.SemesterMarks
import com.vereshchagin.nikolay.stankinschedule.journal.core.domain.model.Student
import com.vereshchagin.nikolay.stankinschedule.journal.core.domain.model.StudentCredentials
import com.vereshchagin.nikolay.stankinschedule.journal.core.domain.repository.JournalServiceRepository
import retrofit2.await
import javax.inject.Inject

class JournalServiceRepositoryImpl @Inject constructor(
    private val api: ModuleJournalAPI,
) : JournalServiceRepository {

    override suspend fun loadSemesters(
        credentials: StudentCredentials,
    ): Student {
        return api.getSemesters(credentials.login, credentials.password)
            .await()
            .toStudent()
    }

    override suspend fun loadMarks(
        credentials: StudentCredentials,
        semester: String,
    ): SemesterMarks {
        return api.getMarks(credentials.login, credentials.password, semester)
            .await()
            .toSemesterMarks()
    }
}