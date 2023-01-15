package com.vereshchagin.nikolay.stankinschedule.schedule.table.domain.model

class ScheduleTableCell(
    val row: Int,
    val column: Int,
    val text: String,
    val rowSpan: Int,
    val columnSpan: Int
)