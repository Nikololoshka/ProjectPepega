package com.vereshchagin.nikolay.stankinschedule.journal.login.domain.usecase

import com.vereshchagin.nikolay.stankinschedule.journal.core.data.mapper.toStudent
import com.vereshchagin.nikolay.stankinschedule.journal.core.domain.model.Student
import com.vereshchagin.nikolay.stankinschedule.journal.core.domain.model.StudentCredentials
import com.vereshchagin.nikolay.stankinschedule.journal.core.domain.repository.JournalSecureRepository
import com.vereshchagin.nikolay.stankinschedule.journal.core.domain.repository.JournalServiceRepository
import com.vereshchagin.nikolay.stankinschedule.journal.core.domain.repository.JournalStorageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val service: JournalServiceRepository,
    private val secure: JournalSecureRepository,
    private val storage: JournalStorageRepository,
) {
    fun login(login: String, password: String): Flow<Student> = flow {
        val possibleCredentials = StudentCredentials(login, password)

        // Если не верны, то тут будет исключение с Http 401
        val student = service.loadSemesters(possibleCredentials).toStudent()

        secure.signIn(possibleCredentials)
        storage.saveStudent(student)

        emit(student)
    }.flowOn(Dispatchers.IO)
}