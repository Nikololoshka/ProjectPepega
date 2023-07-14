package com.vereshchagin.nikolay.stankinschedule.schedule.ical.domain.model

class ICalCalendar(
    val name: String,
    val prodId: String = "-//Unknown//Stankin schedule v2.1//RU",
    val timeZone: String = "Europe/Moscow",
    val timeZoneName: String = "MSK",
    val timeZoneOffset: String = "+0300",
    val events: List<ICalEvent>
)