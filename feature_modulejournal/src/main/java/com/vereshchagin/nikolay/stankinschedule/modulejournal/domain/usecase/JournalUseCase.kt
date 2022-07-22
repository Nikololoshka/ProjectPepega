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
    private val service: JournalServiceRepository,
    private val storage: JournalStorageRepository,
) {

    fun student(): Flow<Student> = flow {

        val cache = storage.loadStudent()
        if (cache != null) {
            emit(cache.data)
        }

    }.flowOn(Dispatchers.IO)

    fun semesterMarks(semester: String): Flow<SemesterMarks> = flow {

        val cache = storage.loadSemester(semester)
        if (cache != null) {
            emit(cache.data)

        } else {
            val marks = service.loadMarks("621522", "stankin621522", semester)
                .toSemesterMarks()

            Log.d("JournalUseCase", marks.toString())
            storage.saveSemester(semester, marks)
            Log.d("JournalUseCase", "Saved")

            emit(marks)
        }
    }.flowOn(Dispatchers.IO)
}