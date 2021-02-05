package com.vereshchagin.nikolay.stankinschedule.ui.schedule.view

import android.app.Application
import android.net.Uri
import androidx.lifecycle.*
import androidx.paging.*
import com.vereshchagin.nikolay.stankinschedule.model.schedule.Schedule
import com.vereshchagin.nikolay.stankinschedule.repository.ScheduleRepository
import com.vereshchagin.nikolay.stankinschedule.settings.ApplicationPreference
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.view.paging.ScheduleViewDay
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.view.paging.ScheduleViewDaySource
import com.vereshchagin.nikolay.stankinschedule.utils.State
import kotlinx.coroutines.launch
import org.joda.time.LocalDate

/**
 * ViewModel для просмотра расписания.
 */
class ScheduleViewViewModel(
    private var scheduleName: String,
    private val startDate: LocalDate?,
    application: Application
) : AndroidViewModel(application) {

    /**
     * Состояние загрузки расписания.
     */
    val state = MutableLiveData<State<Boolean>>(State.loading())

    /**
     * Репозиторий с расписанием.
     */
    private val repository = ScheduleRepository()
    private lateinit var schedule: Schedule

    /**
     * LiveData с днями расписания.
     */
    private val refreshTrigger = MutableLiveData<LocalDate>()
    val scheduleDays = Transformations.switchMap(refreshTrigger) { refresh(it) }

    init {
        viewModelScope.launch {
            loadSchedule(startDate ?: LocalDate.now())
        }
    }

    /**
     * Загружает расписание для просмотра.
     */
    private fun loadSchedule(initKey: LocalDate) {
        state.value = State.loading()
        try {
            schedule = repository.load(scheduleName, getApplication())

        } catch (e: Exception) {
            state.value = State.failed(e)
            return
        }
        state.value = State.success(schedule.isEmpty())
        refreshTrigger.value = initKey
    }

    /**
     * Обновляет pager с днями расписания.
     */
    private fun refresh(
        initKey: LocalDate = LocalDate.now()
    ): LiveData<PagingData<ScheduleViewDay>> {
        if (state.value is State.Loading) {
            return MutableLiveData(PagingData.empty())
        }

        val limit = ApplicationPreference.scheduleLimit(getApplication())
        val pager = Pager(
            PagingConfig(
                pageSize = PAGE_SIZE,
                prefetchDistance = PAGE_SIZE / 2,
                initialLoadSize = PAGE_SIZE,
                maxSize = PAGE_SIZE * 8
            ),
            if (limit) schedule.limitDate(initKey) else initKey,
        ) {
            ScheduleViewDaySource(schedule)
        }

        return pager.liveData.cachedIn(viewModelScope)
    }

    /**
     * Обновляет pager, где изначальной позицией будет передаваемая дата.
     */
    fun updatePagerView(scrollDate: LocalDate) {
        fakeRefresh()

        state.value = State.success(schedule.isEmpty())
        refreshTrigger.value = scrollDate
    }

    /**
     *  Перезагружает расписание и pager связанный с ним.
     *  В качестве изначальной позицией будет передаваемая дата.
     */
    fun refreshPagerView(newScheduleName: String, scrollDate: LocalDate) {
        scheduleName = newScheduleName
        fakeRefresh()

        viewModelScope.launch {
            loadSchedule(scrollDate)
        }
    }

    /**
     *  Сброс текущего расписания.
     *
     *  Для сброса всего списка вызывается "пустое" обновление.
     *  Без него при асинхронном обновлении списка в адаптере возможен случай, когда
     *  отображается не тот день, а смещенный на +- PAGE_SIZE.
     */
    private fun fakeRefresh() {
        state.value = State.loading()
        refreshTrigger.value = LocalDate.now()
    }

    /**
     * Сохраняет расписание на устройство по заданному пути.
     */
    fun saveScheduleToDevice(uri: Uri) {
        repository.copy(scheduleName, schedule, uri, getApplication())
    }

    /**
     * Возвращает текущие расписание.
     */
    fun currentSchedule(): Schedule? {
        if (this::schedule.isInitialized) {
            return schedule
        }
        return null
    }

    /**
     * Factory для создания ViewModel.
     */
    class Factory(
        private val scheduleName: String,
        private val startDate: LocalDate?,
        private val application: Application,
    ) : ViewModelProvider.NewInstanceFactory() {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return ScheduleViewViewModel(scheduleName, startDate, application) as T
        }
    }

    companion object {
        /**
         * Размер страницы погрузки.
         */
        private const val PAGE_SIZE = 10
    }
}