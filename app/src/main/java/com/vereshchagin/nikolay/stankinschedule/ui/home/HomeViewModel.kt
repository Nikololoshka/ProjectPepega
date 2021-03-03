package com.vereshchagin.nikolay.stankinschedule.ui.home

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.*
import com.vereshchagin.nikolay.stankinschedule.model.home.HomeScheduleData
import com.vereshchagin.nikolay.stankinschedule.model.schedule.Schedule
import com.vereshchagin.nikolay.stankinschedule.model.schedule.db.PairItem
import com.vereshchagin.nikolay.stankinschedule.model.schedule.pair.Subgroup
import com.vereshchagin.nikolay.stankinschedule.repository.NewsHomeRepository
import com.vereshchagin.nikolay.stankinschedule.repository.ScheduleRepository
import com.vereshchagin.nikolay.stankinschedule.settings.ApplicationPreference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.joda.time.LocalDate

/**
 * ViewModel для фрагмента главной страницы.
 */
class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val scheduleRepository = ScheduleRepository(application)
    private val newsRepository = NewsHomeRepository(application)

    val scheduleData = MutableLiveData<HomeScheduleData>(null)
    val newsData = newsRepository.latest()

    private var scheduleSettings = ApplicationPreference.homeScheduleSettings(application)

    init {
        viewModelScope.launch(Dispatchers.IO) {
            startUpdateSchedule()
            newsRepository.updateAll()
        }
    }

    @SuppressLint("DefaultLocale")
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
            titles.add(start.toString("EEEE, dd MMMM").capitalize())

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

        // нет избранного расписания
        if (scheduleSettings.favorite.isEmpty()) {
            scheduleData.postValue(HomeScheduleData.empty())
        } else {
            val schedule = scheduleRepository.schedule(scheduleSettings.favorite)
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

    /**
     * Factory для создания ViewModel
     */
    class Factory(private val application: Application) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return HomeViewModel(application) as T
        }
    }
}