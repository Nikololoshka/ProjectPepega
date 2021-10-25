package com.vereshchagin.nikolay.stankinschedule.ui.home.schedule

import com.vereshchagin.nikolay.stankinschedule.model.schedule.db.PairItem

/**
 * Результат загрузки расписания на главной странице.
 */
class HomeScheduleData(
    val scheduleName: String?,
    val titles: ArrayList<String>,
    val pairs: ArrayList<ArrayList<PairItem>>,
) {

    fun isEmpty(): Boolean = scheduleName == null && titles.isEmpty() && pairs.isEmpty()

    companion object {
        /**
         * Возвращает пустой результат.
         */
        fun empty() = HomeScheduleData(null, ArrayList(), ArrayList())
    }
}