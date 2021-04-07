package com.vereshchagin.nikolay.stankinschedule.ui.schedule.view

import android.app.Application
import android.net.Uri
import androidx.lifecycle.*
import androidx.paging.*
import com.vereshchagin.nikolay.stankinschedule.model.schedule.Schedule
import com.vereshchagin.nikolay.stankinschedule.model.schedule.db.ScheduleItem
import com.vereshchagin.nikolay.stankinschedule.repository.ScheduleRepository
import com.vereshchagin.nikolay.stankinschedule.settings.ApplicationPreference
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.view.paging.ScheduleViewDay
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.view.paging.ScheduleViewDaySource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.joda.time.LocalDate

/**
 * ViewModel для просмотра расписания.
 */
class ScheduleViewViewModel(
    private var scheduleName: String,
    private val startDate: LocalDate?,
    application: Application,
) : AndroidViewModel(application) {

    enum class ScheduleState {
        LOADING,
        SUCCESSFULLY_LOADED,
        SUCCESSFULLY_LOADED_EMPTY,
        NOT_EXIST,
    }

    enum class ScheduleActionState {
        NONE,
        RENAMED,
        REMOVED,
        EXPORTED
    }

    /**
     * Состояние загрузки расписания.
     */
    val scheduleState = MutableLiveData(ScheduleState.LOADING)
    val actionState = MutableLiveData(ScheduleActionState.NONE)

    /**
     * Репозиторий с расписанием.
     */
    private val repository = ScheduleRepository(application)
    private var schedule: Schedule? = null

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
    private suspend fun loadSchedule(initKey: LocalDate) {
        scheduleState.postValue(ScheduleState.LOADING)
        val item = repository.scheduleItem(scheduleName).first()
        if (item == null) {
            scheduleState.postValue(ScheduleState.NOT_EXIST)
            return
        }

        repository.schedule(item.id)
            .filterNotNull()
            .collect {
                this.schedule = it
                refreshTrigger.value = initKey
            }
    }

    /**
     * Обновляет pager с днями расписания.
     */
    private fun refresh(
        initKey: LocalDate = LocalDate.now(),
    ): LiveData<PagingData<ScheduleViewDay>> {
        val currentSchedule = schedule
        if (currentSchedule == null) {
            scheduleState.value = ScheduleState.SUCCESSFULLY_LOADED
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
            if (limit) schedule?.limitDate(initKey) else initKey,
        ) {
            ScheduleViewDaySource(currentSchedule)
        }

        scheduleState.value = if (currentSchedule.isEmpty()) {
            ScheduleState.SUCCESSFULLY_LOADED_EMPTY
        } else {
            ScheduleState.SUCCESSFULLY_LOADED
        }

        return pager.liveData.cachedIn(viewModelScope)
    }

    /**
     * Обновляет pager, где изначальной позицией будет передаваемая дата.
     */
    fun updatePagerView(scrollDate: LocalDate) {
        scheduleState.value = ScheduleState.LOADING
        refreshTrigger.value = scrollDate
    }

    fun renameSchedule(newScheduleName: String) {
        val currentScheduleItem = schedule?.info ?: return

        viewModelScope.launch(Dispatchers.IO) {
            currentScheduleItem.scheduleName = newScheduleName
            scheduleName = newScheduleName
            repository.updateScheduleItem(currentScheduleItem)

            actionState.postValue(ScheduleActionState.RENAMED)
        }
    }

    fun removeSchedule() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.removeSchedule(scheduleName)
            actionState.postValue(ScheduleActionState.REMOVED)
        }
    }

    /**
     * Сохраняет расписание на устройство по заданному пути.
     */
    fun saveScheduleToDevice(uri: Uri) {
        viewModelScope.launch {
            repository.saveToDevice(scheduleName, uri, getApplication())
        }
    }

    /**
     * Дата начала расписания
     */
    fun startDate(): LocalDate? {
        return schedule?.startDate()
    }

    /**
     * Дата конца расписания.
     */
    fun endDate(): LocalDate? {
        return schedule?.endDate()
    }

    /**
     * Является ли текущие расписание синхронизированным.
     */
    fun isSynced(): Boolean {
        return schedule?.info?.synced ?: false
    }

    /**
     * Отключает синхронизацию расписания.
     */
    fun disableScheduleSynced() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.toggleScheduleSyncState(scheduleName, false)
        }
    }

    /**
     * Возвращает информацию о расписании.
     */
    fun info(): ScheduleItem? {
        return schedule?.info
    }

    fun actionComplete() {
        actionState.value = ScheduleActionState.NONE
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