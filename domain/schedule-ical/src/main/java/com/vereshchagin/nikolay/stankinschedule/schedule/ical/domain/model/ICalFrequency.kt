package com.vereshchagin.nikolay.stankinschedule.schedule.ical.domain.model

sealed interface ICalFrequency {
    class ICalWeekly(val interval: Int) : ICalFrequency
}