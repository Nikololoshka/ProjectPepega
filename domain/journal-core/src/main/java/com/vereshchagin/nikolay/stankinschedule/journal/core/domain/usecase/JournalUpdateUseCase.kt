package com.vereshchagin.nikolay.stankinschedule.journal.core.domain.usecase

import com.vereshchagin.nikolay.stankinschedule.core.domain.ext.subHours
import com.vereshchagin.nikolay.stankinschedule.journal.core.domain.model.MarkType
import com.vereshchagin.nikolay.stankinschedule.journal.core.domain.model.Student
import com.vereshchagin.nikolay.stankinschedule.journal.core.domain.repository.JournalRepository
import com.vereshchagin.nikolay.stankinschedule.journal.core.domain.repository.JournalStorageRepository
import org.joda.time.DateTime
import javax.inject.Inject

class JournalUpdateUseCase @Inject constructor(
    private val journal: JournalRepository,
    private val storage: JournalStorageRepository
) {

    suspend fun updateSemesters(): Pair<Student?, Set<String>> {
        val cache = storage.loadStudent()
        if (cache == null || (cache.cacheTime subHours DateTime.now() < 1)) {
            return cache?.data to emptySet()
        }

        val newStudent = journal.student(useCache = false)
        return newStudent to newStudent.semesters.subtract(cache.data.semesters.toSet())
    }

    suspend fun updateSemesterMarks(semester: String): Set<String> {
        val cache = storage.loadSemester(semester)
        if (cache == null || (cache.cacheTime subHours DateTime.now() < 1)) {
            return emptySet()
        }

        val newSemesterMarks = journal.semesterMarks(semester, useCache = false)

        val changes = mutableSetOf<String>()
        for (newDiscipline in newSemesterMarks) {
            for (oldDiscipline in cache.data) {
                if (newDiscipline.title == oldDiscipline.title) {
                    for (type in MarkType.values()) {
                        val newMark = newDiscipline[type]
                        val oldMark = oldDiscipline[type]
                        if (newMark != null && oldMark != null && newMark != oldMark) {
                            changes += "${newDiscipline.title}: $newMark (${type.tag})"
                        }
                    }
                }
            }
        }

        return changes
    }
}