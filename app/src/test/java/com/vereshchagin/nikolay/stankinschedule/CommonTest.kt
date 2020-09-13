package com.vereshchagin.nikolay.stankinschedule

import com.vereshchagin.nikolay.stankinschedule.model.schedule.pair.DateSingle
import org.joda.time.LocalDate
import org.junit.Assert
import org.junit.Test

/**
 * Общие тесты.
 */
class CommonTest {
    @Test
    fun equals() {
        val date = LocalDate.now().plusDays(1)
        val d1 = DateSingle(date)
        val d2 = DateSingle(date)
        Assert.assertTrue(d1.intersect(d2))
    }
}