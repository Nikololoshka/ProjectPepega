package com.vereshchagin.nikolay.stankinschedule.journal.core.data.mapper

import com.vereshchagin.nikolay.stankinschedule.journal.core.data.model.MarkResponse
import com.vereshchagin.nikolay.stankinschedule.journal.core.data.model.SemestersResponse
import com.vereshchagin.nikolay.stankinschedule.journal.core.domain.model.SemesterMarks
import com.vereshchagin.nikolay.stankinschedule.journal.core.domain.model.Student


fun SemestersResponse.toStudent(): Student {
    return Student(
        name = "$surname $initials",
        group = group,
        semesters = semesters.reversed()
    )
}

fun List<MarkResponse>.toSemesterMarks(): SemesterMarks {
    return SemesterMarks().apply {
        this@toSemesterMarks.forEach { mark ->
            addMark(mark.title, mark.type, mark.value, mark.factor)
        }
    }
}