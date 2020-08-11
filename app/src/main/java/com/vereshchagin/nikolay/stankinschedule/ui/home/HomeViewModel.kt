package com.vereshchagin.nikolay.stankinschedule.ui.home

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.*
import com.vereshchagin.nikolay.stankinschedule.model.home.HomeScheduleData
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.model.Schedule
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.model.pair.Pair
import com.vereshchagin.nikolay.stankinschedule.ui.settings.SchedulePreference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.apache.commons.io.FileUtils
import org.joda.time.DateTime
import java.io.File
import java.nio.charset.StandardCharsets

/**
 * ViewModel для фрагмента главной страницы.
 */
class HomeViewModel(application: Application) : AndroidViewModel(application) {

    val scheduleData = MutableLiveData<HomeScheduleData>(null)

    init {
        viewModelScope.launch(Dispatchers.IO) {
            loadSchedule()
        }
    }

    @SuppressLint("DefaultLocale")
    private fun loadSchedule() {
        val favorite = SchedulePreference.favorite(getApplication())
        if (favorite == null || favorite.isEmpty()) {
            scheduleData.postValue(HomeScheduleData.empty())
            return
        }

        val path = SchedulePreference.createPath(getApplication(), favorite)
        val schedule: Schedule
        try {
            val file = File(path)
            val json = FileUtils.readFileToString(file, StandardCharsets.UTF_8)
            schedule = Schedule.fromJson(json)

        } catch (ignored: Exception) {
            scheduleData.postValue(HomeScheduleData.empty())
            return
        }

        var start = DateTime.now().withTimeAtStartOfDay().minusDays(2)

        val titles = ArrayList<String>(5)
        val pairs = ArrayList<ArrayList<Pair>>(5)

        for (i in 0..4) {
            val list = ArrayList<Pair>()
            schedule.pairsByDate(start.toGregorianCalendar()).toCollection(list)
            pairs.add(list)
            titles.add(start.toString("EEEE, dd MMMM").capitalize())

            start = start.plusDays(1)
        }

        scheduleData.postValue(
            HomeScheduleData(
                titles,
                pairs
            )
        )
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