package com.vereshchagin.nikolay.stankinschedule.journal.core.data.repository

import android.util.Log
import com.vereshchagin.nikolay.stankinschedule.core.domain.ext.subHours
import com.vereshchagin.nikolay.stankinschedule.core.domain.ext.subMinutes
import com.vereshchagin.nikolay.stankinschedule.journal.core.data.BuildConfig
import com.vereshchagin.nikolay.stankinschedule.journal.core.domain.exceptions.StudentAuthorizedException
import com.vereshchagin.nikolay.stankinschedule.journal.core.domain.model.SemesterMarks
import com.vereshchagin.nikolay.stankinschedule.journal.core.domain.model.Student
import com.vereshchagin.nikolay.stankinschedule.journal.core.domain.repository.JournalRepository
import com.vereshchagin.nikolay.stankinschedule.journal.core.domain.repository.JournalSecureRepository
import com.vereshchagin.nikolay.stankinschedule.journal.core.domain.repository.JournalServiceRepository
import com.vereshchagin.nikolay.stankinschedule.journal.core.domain.repository.JournalStorageRepository
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.joda.time.DateTime
import javax.inject.Inject

class JournalRepositoryImpl @Inject constructor(
    private val service: JournalServiceRepository,
    private val secure: JournalSecureRepository,
    private val storage: JournalStorageRepository,
) : JournalRepository {

    private val semesterLocker = Mutex()

    override suspend fun student(useCache: Boolean): Student? {

        val cache = storage.loadStudent()
        if (cache != null && useCache && (cache.cacheTime subHours DateTime.now() < 24)) {
            if (BuildConfig.DEBUG) {
                val minutes = cache.cacheTime subMinutes DateTime.now()
                Log.d(TAG, "Student cache time: $minutes minute(-s)")
            }

            return cache.data
        }

        return try {
            val studentCredentials = secure.signCredentials()
            val student = service.loadSemesters(studentCredentials)
            storage.saveStudent(student)

            student

        } catch (e: StudentAuthorizedException) {
            null
        }
    }

    override suspend fun semesterMarks(
        semester: String,
        semesterExpireHours: Int,
        useCache: Boolean,
    ): SemesterMarks = semesterLocker.withLock {

        val cache = storage.loadSemester(semester)
        if (cache != null && useCache && (cache.cacheTime subHours DateTime.now() < semesterExpireHours)) {
            if (BuildConfig.DEBUG) {
                val hours = cache.cacheTime subHours DateTime.now()
                Log.d(TAG, "Semester '$semester' cache time: $hours hour(-s)")
            }

            return cache.data
        }

        val studentCredentials = secure.signCredentials()
        val marks = service.loadMarks(studentCredentials, semester)
        storage.saveSemester(semester, marks)

        return marks
    }

    companion object {
        private const val TAG = "JournalRepositoryImpl"
    }
}