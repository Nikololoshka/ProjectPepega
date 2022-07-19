package com.vereshchagin.nikolay.stankinschedule.modulejournal.domain.usecase

import com.vereshchagin.nikolay.stankinschedule.core.ui.State
import com.vereshchagin.nikolay.stankinschedule.modulejournal.data.mapper.toStudent
import com.vereshchagin.nikolay.stankinschedule.modulejournal.domain.model.Student
import com.vereshchagin.nikolay.stankinschedule.modulejournal.domain.repository.JournalServiceRepository
import com.vereshchagin.nikolay.stankinschedule.modulejournal.domain.repository.JournalStorageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val serviceRepository: JournalServiceRepository,
    private val storageRepository: JournalStorageRepository
) {

    suspend fun isLogging(): Boolean {
        return storageRepository.loadStudent() != null
    }

    fun login(login: String, password: String): Flow<Student> = flow {
        val student = serviceRepository.loadSemesters(login, password).toStudent()
        storageRepository.saveStudent(student)
        emit(student)
    }.flowOn(Dispatchers.IO)
}