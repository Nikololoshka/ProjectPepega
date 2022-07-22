package com.vereshchagin.nikolay.stankinschedule.modulejournal.data.mapper

import com.vereshchagin.nikolay.stankinschedule.modulejournal.data.api.MarkResponse
import com.vereshchagin.nikolay.stankinschedule.modulejournal.data.api.SemestersResponse
import com.vereshchagin.nikolay.stankinschedule.modulejournal.domain.model.SemesterMarks
import com.vereshchagin.nikolay.stankinschedule.modulejournal.domain.model.Student

fun SemestersResponse.toStudent(): Student {
    return Student(
        name = "$surname $initials",
        group = group,
        semesters = semesters
    )
}

fun List<MarkResponse>.toSemesterMarks(): SemesterMarks {
    return SemesterMarks().apply {
        forEach { mark ->
            addMark(mark.title, mark.type, mark.value, mark.factor)
        }
    }
}