package com.vereshchagin.nikolay.stankinschedule.schedule.data.api

class PairResponse(
    val title: String,
    val lecturer: String,
    val classroom: String,
    val type: String,
    val subgroup: String,
    val time: String,
    val date: DateResponse,
) {
    class DateResponse(
        val frequency: String,
        val date: String,
    )
}