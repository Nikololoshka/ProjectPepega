package com.vereshchagin.nikolay.stankinschedule.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.vereshchagin.nikolay.stankinschedule.model.home.HomeScheduleData
import com.vereshchagin.nikolay.stankinschedule.model.schedule.Schedule
import com.vereshchagin.nikolay.stankinschedule.model.schedule.db.PairItem
import com.vereshchagin.nikolay.stankinschedule.model.schedule.pair.Subgroup
import com.vereshchagin.nikolay.stankinschedule.repository.NewsRepository
import com.vereshchagin.nikolay.stankinschedule.repository.ScheduleRepository
import com.vereshchagin.nikolay.stankinschedule.settings.ApplicationPreference
import com.vereshchagin.nikolay.stankinschedule.utils.extensions.toTitleString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.joda.time.LocalDate
import javax.inject.Inject

/**
 * ViewModel для фрагмента главной страницы.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    application: Application,
    private val scheduleRepository: ScheduleRepository,
    private val newsRepository: NewsRepository
) : AndroidViewModel(application) {


    val scheduleData = MutableLiveData<HomeScheduleData>(null)
    val newsData = newsRepository.latest()

    private var scheduleSettings = ApplicationPreference.homeScheduleSettings(application)

    init {
        viewModelScope.launch(Dispatchers.IO) {
            startUpdateSchedule()
            newsRepository.updateAll()
        }
    }

    private fun updateDataFromSchedule(schedule: Schedule) {
        val count = scheduleSettings.delta * 2 + 1
        var start = LocalDate.now().minusDays(scheduleSettings.delta)
        val titles = ArrayList<String>(count)
        val pairs = ArrayList<ArrayList<PairItem>>(count)

        for (i in 0 until count) {
            val list = ArrayList<PairItem>()
            schedule.pairsByDate(start).filter {
                it.isCurrently(scheduleSettings.subgroup)
            }.toCollection(list)

            pairs.add(list)
            titles.add(start.toString("EEEE, dd MMMM").toTitleString())

            start = start.plusDays(1)
        }

        var scheduleName = scheduleSettings.favorite
        // добавление подгруппы к названию
        if (scheduleSettings.subgroup != Subgroup.COMMON && scheduleSettings.display) {
            scheduleName += " ${scheduleSettings.subgroup.toString(getApplication())}"
        }

        scheduleData.postValue(
            HomeScheduleData(
                scheduleName,
                titles,
                pairs
            )
        )
    }

    private suspend fun startUpdateSchedule() {
        scheduleSettings = ApplicationPreference.homeScheduleSettings(getApplication())

        val favorite = scheduleSettings.favorite
        // нет избранного расписания
        if (favorite.isNullOrEmpty()) {
            scheduleData.postValue(HomeScheduleData.empty())
        } else {
            val schedule = scheduleRepository.schedule(favorite)
                .filterNotNull()
                .first()

            updateDataFromSchedule(schedule)
        }
    }

    /**
     * Обновляет расписание.
     */
    fun updateSchedule() {
        viewModelScope.launch(Dispatchers.IO) {
            startUpdateSchedule()
        }
    }

    /**
     * Проверяет, правильное количество дней загружено и отображается ли нужная подгруппа.
     * Если нет, то обновляем данные расписания.
     */
    fun checkScheduleData() {
        val newSettings = ApplicationPreference.homeScheduleSettings(getApplication())
        if (scheduleSettings != newSettings) {
            updateSchedule()
        }
    }
}