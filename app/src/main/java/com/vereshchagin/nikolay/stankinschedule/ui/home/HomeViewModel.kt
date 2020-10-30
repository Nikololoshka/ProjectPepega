package com.vereshchagin.nikolay.stankinschedule.ui.home

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.*
import com.vereshchagin.nikolay.stankinschedule.model.home.HomeScheduleData
import com.vereshchagin.nikolay.stankinschedule.model.schedule.Schedule
import com.vereshchagin.nikolay.stankinschedule.model.schedule.pair.Pair
import com.vereshchagin.nikolay.stankinschedule.model.schedule.pair.Subgroup
import com.vereshchagin.nikolay.stankinschedule.repository.NewsHomeRepository
import com.vereshchagin.nikolay.stankinschedule.repository.ScheduleRepository
import com.vereshchagin.nikolay.stankinschedule.ui.settings.ApplicationPreference
import com.vereshchagin.nikolay.stankinschedule.ui.settings.SchedulePreference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.joda.time.LocalDate

/**
 * ViewModel для фрагмента главной страницы.
 */
class HomeViewModel(application: Application) : AndroidViewModel(application) {


    private val scheduleRepository = ScheduleRepository()
    private val newsRepository = NewsHomeRepository(application)

    val scheduleData = MutableLiveData<HomeScheduleData>(null)
    val newsData = newsRepository.latest()

    private var delta = 2
    private var subgroup = Subgroup.COMMON
    private var isDisplay = true

    init {
        viewModelScope.launch(Dispatchers.IO) {
            loadSchedule()
            newsRepository.updateAll()
        }
    }

    @SuppressLint("DefaultLocale")
    private fun loadSchedule() {
        val favorite = SchedulePreference.favorite(getApplication())
        if (favorite == null || favorite.isEmpty()) {
            scheduleData.postValue(HomeScheduleData.empty())
            return
        }

        val schedule: Schedule
        try {
            schedule = scheduleRepository.load(favorite, getApplication())

        } catch (ignored: Exception) {
            scheduleData.postValue(HomeScheduleData.empty())
            return
        }

        delta = ApplicationPreference.homeScheduleDelta(getApplication())
        val count = delta * 2 + 1
        var start = LocalDate.now().minusDays(delta)

        val titles = ArrayList<String>(count)
        val pairs = ArrayList<ArrayList<Pair>>(count)
        subgroup = ApplicationPreference.subgroup(getApplication())

        for (i in 0 until count) {
            val list = ArrayList<Pair>()
            schedule.pairsByDate(start).filter {
                it.isCurrently(subgroup)
            }.toCollection(list)

            pairs.add(list)
            titles.add(start.toString("EEEE, dd MMMM").capitalize())

            start = start.plusDays(1)
        }

        isDisplay = ApplicationPreference.displaySubgroup(getApplication())
        val scheduleName = if (subgroup != Subgroup.COMMON && isDisplay) {
            "$favorite ${subgroup.toString(getApplication())}"
        } else {
            favorite
        }

        scheduleData.postValue(
            HomeScheduleData(
                scheduleName,
                titles,
                pairs
            )
        )
    }

    /**
     * Обновляет расписание.
     */
    fun updateSchedule() {
        viewModelScope.launch(Dispatchers.IO) {
            loadSchedule()
        }
    }

    /**
     * Проверяет, правильное количество дней загружено и отображается ли нужная погруппа.
     */
    fun isScheduleDataValid(): Boolean {
        val newDelta = ApplicationPreference.homeScheduleDelta(getApplication())
        if (newDelta != delta) {
            return false
        }
        val newSubgroup = ApplicationPreference.subgroup(getApplication())
        if (newSubgroup != subgroup) {
            return false
        }

        val newDisplay = ApplicationPreference.displaySubgroup(getApplication())
        if (newDisplay != isDisplay) {
            return false
        }

        return true
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