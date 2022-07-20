package com.vereshchagin.nikolay.stankinschedule.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.vereshchagin.nikolay.stankinschedule.model.schedule.Schedule
import com.vereshchagin.nikolay.stankinschedule.model.schedule.db.PairItem
import com.vereshchagin.nikolay.stankinschedule.model.schedule.pair.Subgroup
import com.vereshchagin.nikolay.stankinschedule.repository.NewsRepository
import com.vereshchagin.nikolay.stankinschedule.repository.ScheduleRepository
import com.vereshchagin.nikolay.stankinschedule.settings.ApplicationPreferenceKt
import com.vereshchagin.nikolay.stankinschedule.ui.home.schedule.HomeScheduleData
import com.vereshchagin.nikolay.stankinschedule.utils.ScheduleUtils
import com.vereshchagin.nikolay.stankinschedule.utils.extensions.toTitleString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    private val newsRepository: NewsRepository,
    private val preference: ApplicationPreferenceKt,
) : AndroidViewModel(application) {


    private val _scheduleData = MutableStateFlow<HomeScheduleData?>(null)

    /**
     * Данные для расписания на главной.
     */
    val scheduleData = _scheduleData.asStateFlow()

    /**
     * Данные для списка новостей на главной.
     */
    val newsData = newsRepository.latest()

    /**
     * Текущие настройки отображаемого расписания.
     */
    private var currentScheduleSettings: ScheduleSettings? = null

    init {
        viewModelScope.launch(Dispatchers.IO) {
            startUpdateSchedule()
            newsRepository.updateAll()
        }
    }

    /**
     * Обновляет данные для Pager с расписанием.
     * @param schedule текущие расписание.
     * @param settings настройки отображения.
     */
    private fun updateDataFromSchedule(
        schedule: Schedule,
        settings: ScheduleSettings,
    ): HomeScheduleData {
        val count = settings.delta * 2 + 1
        var start = LocalDate.now().minusDays(settings.delta)
        val titles = ArrayList<String>(count)
        val pairs = ArrayList<ArrayList<PairItem>>(count)

        for (i in 0 until count) {
            val list = ArrayList<PairItem>()
            schedule.pairsByDate(start).filter {
                it.isCurrently(settings.subgroup)
            }.toCollection(list)

            pairs.add(list)
            titles.add(start.toString("EEEE, dd MMMM").toTitleString())

            start = start.plusDays(1)
        }

        var scheduleName = schedule.info.scheduleName

        // добавление подгруппы к названию
        if (settings.subgroup != Subgroup.COMMON && settings.isDisplaySubgroup) {
            scheduleName += " " + ScheduleUtils.subgroupToString(
                settings.subgroup, getApplication()
            )
        }

        return HomeScheduleData(scheduleName, titles, pairs)
    }

    /**
     * Запускает обновления данных для Pager с расписанием.
     */
    private suspend fun startUpdateSchedule() {
        val settings = ScheduleSettings(
            scheduleRepository.favoriteScheduleId,
            preference.scheduleSubgroup,
            preference.isSubgroupDisplay,
            preference.scheduleDelta
        )

        // изменились ли настройки отображения
        if (currentScheduleSettings == settings) return
        currentScheduleSettings = settings

        // нет избранного расписания
        _scheduleData.value = if (settings.favoriteId == ScheduleRepository.NO_SCHEDULE) {
            HomeScheduleData.empty()

        } else {
            val schedule = scheduleRepository.schedule(settings.favoriteId).first()
            if (schedule == null) {
                HomeScheduleData.empty()

            } else {
                updateDataFromSchedule(schedule, settings)
            }
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
     * Возвращает ID избранного расписания.
     */
    fun favoriteScheduleId() = scheduleRepository.favoriteScheduleId

    /**
     * Настройки pager с расписанием для отображения.
     */
    private class ScheduleSettings(
        val favoriteId: Long,
        val subgroup: Subgroup,
        val isDisplaySubgroup: Boolean,
        val delta: Int,
    )
}