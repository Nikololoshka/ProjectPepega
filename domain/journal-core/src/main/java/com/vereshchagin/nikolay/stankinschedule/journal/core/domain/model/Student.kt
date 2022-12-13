package com.vereshchagin.nikolay.stankinschedule.journal.core.domain.model

class Student(
    val name: String,
    val group: String,
    val semesters: List<String>,
) {
    override fun toString(): String {
        return "Student(name='$name', group='$group', semesters=$semesters)"
    }
}