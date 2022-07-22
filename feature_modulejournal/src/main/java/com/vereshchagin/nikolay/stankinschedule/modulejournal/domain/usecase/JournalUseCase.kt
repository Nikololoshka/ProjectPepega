package com.vereshchagin.nikolay.stankinschedule.modulejournal.domain.usecase

import com.vereshchagin.nikolay.stankinschedule.core.ui.subHours
import com.vereshchagin.nikolay.stankinschedule.modulejournal.data.mapper.toSemesterMarks
import com.vereshchagin.nikolay.stankinschedule.modulejournal.data.mapper.toStudent
import com.vereshchagin.nikolay.stankinschedule.modulejournal.domain.model.SemesterMarks
import com.vereshchagin.nikolay.stankinschedule.modulejournal.domain.model.Student
import com.vereshchagin.nikolay.stankinschedule.modulejournal.domain.model.StudentCredentials
import com.vereshchagin.nikolay.stankinschedule.modulejournal.domain.repository.JournalSecureRepository
import com.vereshchagin.nikolay.stankinschedule.modulejournal.domain.repository.JournalServiceRepository
import com.vereshchagin.nikolay.stankinschedule.modulejournal.domain.repository.JournalStorageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.ResponseBody.Companion.toResponseBody
import org.joda.time.DateTime
import retrofit2.HttpException
import retrofit2.Response
import javax.inject.Inject

class JournalUseCase @Inject constructor(
    private val service: JournalServiceRepository,
    private val secure: JournalSecureRepository,
    private val storage: JournalStorageRepository,
) {

    private var cacheCredentials: StudentCredentials? = null

    private suspend fun requireCredentials(): StudentCredentials {
        val currentCredentials = cacheCredentials
        if (currentCredentials != null) {
            return currentCredentials
        }

        val credentials = secure.signCredentials() ?: throw HttpException(
            Response.error<String>(401, "Unauthorized".toResponseBody())
        )
        cacheCredentials = credentials
        return credentials
    }

    fun student(): Flow<Student?> = flow {

        val cache = storage.loadStudent()
        if (cache != null && (cache.cacheTime subHours DateTime.now() < 24)) {
            emit(cache.data)
            return@flow
        }

        val studentCredentials = requireCredentials()
        val student = service.loadSemesters(studentCredentials).toStudent()
        storage.saveStudent(student)

        emit(student)

    }.flowOn(Dispatchers.IO)

    fun semesterMarks(semester: String): Flow<SemesterMarks> = flow {

        val cache = storage.loadSemester(semester)
        if (cache != null && (cache.cacheTime subHours DateTime.now() < 2)) {
            emit(cache.data)
            return@flow
        }

        val studentCredentials = requireCredentials()
        val marks = service.loadMarks(studentCredentials, semester).toSemesterMarks()
        storage.saveSemester(semester, marks)

        emit(marks)

    }.flowOn(Dispatchers.IO)
}