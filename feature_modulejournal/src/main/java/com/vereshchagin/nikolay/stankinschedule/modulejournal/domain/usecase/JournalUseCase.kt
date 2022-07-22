package com.vereshchagin.nikolay.stankinschedule.modulejournal.domain.usecase

import android.util.Log
import com.vereshchagin.nikolay.stankinschedule.modulejournal.data.mapper.toSemesterMarks
import com.vereshchagin.nikolay.stankinschedule.modulejournal.domain.model.SemesterMarks
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

    fun semesterMarks(semester: String): Flow<SemesterMarks> = flow {

        val cached = storageRepository.loadSemester(semester)
        if (cached != null) {
            emit(cached)
        } else {
            val marks = serviceRepository.loadMarks("621522", "stankin621522", semester)
                .toSemesterMarks()

            Log.d("JournalUseCase", marks.toString())
            storageRepository.saveSemester(semester, marks)
            Log.d("JournalUseCase", "Saved")

            emit(marks)
        }
    }.flowOn(Dispatchers.IO)
}