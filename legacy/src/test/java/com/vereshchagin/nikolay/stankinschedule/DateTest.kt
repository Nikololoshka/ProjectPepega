package com.vereshchagin.nikolay.stankinschedule

import com.vereshchagin.nikolay.stankinschedule.model.schedule.pair.*
import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.junit.Assert
import org.junit.Test

/**
 * Тесты для даты в паре.
 */
class DateTest {

    /**
     * Проверка на создание "единственной" даты.
     */
    @Test
    fun createSingleDate() {
        DateSingle("2021.01.16")
        DateSingle("2020.02.29")
        DateSingle(DateTime(2019, 5, 4, 10, 20))
        DateSingle(LocalDate(2020, 11, 5))
    }

    /**
     * Проверка на создание "диапазонов" даты.
     */
    @Test
    fun createRangeDate() {
        DateRange("2019.02.04", "2019.05.20", Frequency.EVERY)
        DateRange("2020.11.18", "2020.12.16", Frequency.THROUGHOUT)
    }

    /**
     * Проверка на день недели "единственной" даты.
     */
    @Test(expected = DateDayOfWeekException::class)
    fun invalidDayOfWeekSingleDate() {
        DateSingle("2021.01.17")
    }

    /**
     * Проверка на день недели "диапазонов" даты.
     */
    @Test(expected = DateDayOfWeekException::class)
    fun invalidDayOfWeekRangeDate() {
        DateRange("2021.01.03", "2021.01.24", Frequency.EVERY)
    }

    /**
     *  Проверка на правильную периодичность "диапазона" даты.
     */
    @Test(expected = DateFrequencyException::class)
    fun invalidDateRangeFrequency() {
        DateRange("2021.01.01", "2021.01.22", Frequency.THROUGHOUT)
    }

    /**
     * Проверка на нулевую периодичность "диапазона" даты.
     */
    @Test(expected = DateFrequencyException::class)
    fun invalidDateRangeFrequencyZero() {
        DateRange("2021.01.22", "2021.01.22", Frequency.EVERY)
    }

    /**
     * Проверка на отрицательную периодичность "диапазона" даты.
     */
    @Test(expected = DateFrequencyException::class)
    fun invalidDateRangeNegativeFrequency() {
        DateRange("2021.01.22", "2021.01.8", Frequency.EVERY)
    }

    /**
     * Проверка на пересечение дат.
     */
    @Test
    fun intersectDate() {
        val range = DateRange("2021.01.8", "2021.01.29", Frequency.EVERY)
        val single = DateSingle("2021.01.15")

        Assert.assertTrue(range.intersect(single))
        Assert.assertTrue(single.intersect(range))

        Assert.assertTrue(single.intersect(single))
        Assert.assertTrue(range.intersect(range))
    }

    /**
     * Проверка на пересечение дат.
     */
    @Test
    fun invalidIntersectDate() {
        val range1 = DateRange("2021.01.8", "2021.01.29", Frequency.EVERY)
        val range2 = DateRange("2021.02.8", "2021.02.22", Frequency.EVERY)

        Assert.assertFalse(range1.intersect(range2))
    }
}