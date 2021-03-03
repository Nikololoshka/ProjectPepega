package com.vereshchagin.nikolay.stankinschedule.model.home

import com.vereshchagin.nikolay.stankinschedule.model.schedule.db.PairItem

/**
 * Результат загрузки расписания на главной странице.
 */
class HomeScheduleData(
    val scheduleName: String?,
    val titles: ArrayList<String>,
    val pairs: ArrayList<ArrayList<PairItem>>,
    val empty: Boolean = false,
) {
    companion object {
        /**
         * Возвращает пустой результат.
         */
        fun empty() =
            HomeScheduleData(
                "",
                ArrayList(),
                ArrayList(),
                true
            )
    }
}