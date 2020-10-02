package com.vereshchagin.nikolay.stankinschedule.model.home

import com.vereshchagin.nikolay.stankinschedule.model.schedule.pair.Pair


class HomeScheduleData (
    val scheduleName: String,
    val titles: ArrayList<String>,
    val pairs: ArrayList<ArrayList<Pair>>,
    val empty: Boolean = false
) {
    companion object {
        fun empty() =
            HomeScheduleData(
                "",
                ArrayList(),
                ArrayList(),
                true
            )
    }
}