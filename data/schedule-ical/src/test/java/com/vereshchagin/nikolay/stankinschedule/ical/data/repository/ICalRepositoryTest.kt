package com.vereshchagin.nikolay.stankinschedule.ical.data.repository

import org.junit.Test

internal class ICalRepositoryTest {

    @Test
    fun createCalendar() {
        val repository = ICalRepository()
        println(repository.createCalendar())
    }
}