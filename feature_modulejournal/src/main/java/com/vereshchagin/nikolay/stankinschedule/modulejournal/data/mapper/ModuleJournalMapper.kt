package com.vereshchagin.nikolay.stankinschedule.modulejournal.data.mapper

import com.vereshchagin.nikolay.stankinschedule.modulejournal.data.api.MarkResponse
import com.vereshchagin.nikolay.stankinschedule.modulejournal.data.api.SemestersResponse
import com.vereshchagin.nikolay.stankinschedule.modulejournal.domain.model.SemesterMarks
import com.vereshchagin.nikolay.stankinschedule.modulejournal.domain.model.Student

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