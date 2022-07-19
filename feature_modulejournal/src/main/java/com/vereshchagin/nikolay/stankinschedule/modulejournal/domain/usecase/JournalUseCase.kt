package com.vereshchagin.nikolay.stankinschedule.modulejournal.domain.usecase

import com.vereshchagin.nikolay.stankinschedule.modulejournal.domain.model.Student
import com.vereshchagin.nikolay.stankinschedule.modulejournal.domain.repository.JournalServiceRepository
import com.vereshchagin.nikolay.stankinschedule.modulejournal.domain.repository.JournalStorageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class JournalUseCase @Inject constructor(
    private val serviceRepository: JournalServiceRepository,
    private val storageRepository: JournalStorageRepository
) {

    fun student(): Flow<Student> = flow {

        val student = storageRepository.loadStudent()
        if (student != null) {
            emit(student)
        }

    }.flowOn(Dispatchers.IO)
}