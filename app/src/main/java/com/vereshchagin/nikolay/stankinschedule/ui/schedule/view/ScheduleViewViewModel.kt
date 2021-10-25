package com.vereshchagin.nikolay.stankinschedule.ui.schedule.view

import android.app.Application
import android.net.Uri
import android.os.Bundle
import androidx.lifecycle.*
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.savedstate.SavedStateRegistryOwner
import com.vereshchagin.nikolay.stankinschedule.model.schedule.Schedule
import com.vereshchagin.nikolay.stankinschedule.model.schedule.db.ScheduleItem
import com.vereshchagin.nikolay.stankinschedule.repository.ScheduleRepository
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.view.paging.ScheduleViewDaySource
import com.vereshchagin.nikolay.stankinschedule.utils.WidgetUtils
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.joda.time.LocalDate

/**
 * ViewModel для просмотра расписания.
 */
// @HiltViewModel
class ScheduleViewViewModel @AssistedInject constructor(
    application: Application,
    private val repository: ScheduleRepository,
    @Assisted private val scheduleId: Long,
    @Assisted private val handle: SavedStateHandle,
    @Assisted startScheduleDate: LocalDate,
) : AndroidViewModel(application) {

    enum class ScheduleState {
        LOADING,
        SUCCESSFULLY_LOADED,
        SUCCESSFULLY_LOADED_EMPTY,
        NOT_EXIST,
    }

    enum class ScheduleActionState {
        RENAMED,
        REMOVED,
        EXPORTED
    }

    private val _actionState = MutableSharedFlow<ScheduleActionState>()
    private val _scheduleState = MutableStateFlow(ScheduleState.LOADING)
    private val _scheduleItem = MutableStateFlow<ScheduleItem?>(null)


    /**
     * Состояние действия над расписания.
     */
    val actionState = _actionState.asSharedFlow()

    /**
     * Состояние загрузки расписания.
     */
    val scheduleState = _scheduleState.asStateFlow()

    /**
     * Информация о текущем расписании.
     */
    val scheduleItem = _scheduleItem.asStateFlow()

    /**
     * Текущая начальная дата для Pager.
     */
    private val currentPagerDate = MutableStateFlow(
        handle.get<LocalDate>(CURRENT_PAGER_DATE) ?: startScheduleDate
    )

    /**
     * Текущие расписание.
     */
    private val currentSchedule = MutableStateFlow<Schedule?>(null)
    val scheduleStartDate get() = currentSchedule.value?.startDate()
    val scheduleEndDate get() = currentSchedule.value?.endDate()

    private val clearListCh = Channel<Unit>(Channel.CONFLATED)

    /**
     * Pager с днями расписания для отображения.
     */
    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val scheduleDays = flowOf(
        clearListCh.receiveAsFlow().map { PagingData.empty() },
        combine(currentPagerDate, currentSchedule) { date, schedule -> date to schedule }
            .flatMapLatest { (date, schedule) ->
                Pager(
                    config = PagingConfig(
                        pageSize = PAGE_SIZE,
                        prefetchDistance = PAGE_SIZE / 2,
                        initialLoadSize = PAGE_SIZE,
                        maxSize = PAGE_SIZE * 50
                    ),
                    initialKey = date,
                    pagingSourceFactory = {
                        ScheduleViewDaySource(schedule)
                    }
                ).flow
            }.cachedIn(viewModelScope)
    ).flattenMerge(2)

    init {
        // информация о расписании
        viewModelScope.launch(Dispatchers.IO) {
            repository.scheduleItem(scheduleId)
                .collect { item ->
                    if (item != null) {
                        _scheduleItem.value = item
                    } else {
                        _scheduleState.value = ScheduleState.NOT_EXIST
                    }
                }
        }

        // расписание
        viewModelScope.launch(Dispatchers.IO) {
            repository.schedule(scheduleId)
                .filterNotNull()
                .collect { schedule ->
                    showPagerForSchedule(schedule)
                }
        }
    }

    private suspend fun showPagerForSchedule(schedule: Schedule) {
        _scheduleState.value = ScheduleState.LOADING

        clearListCh.send(Unit)
        handle.get<LocalDate>(CURRENT_PAGER_DATE)?.let {
            currentPagerDate.value = it
        }
        currentSchedule.value = schedule

        _scheduleState.value = ScheduleState.SUCCESSFULLY_LOADED
    }

    fun showPagerForDate(date: LocalDate) {
        _scheduleState.value = ScheduleState.LOADING

        viewModelScope.launch {
            clearListCh.send(Unit)
            currentPagerDate.value = date

            _scheduleState.value = ScheduleState.SUCCESSFULLY_LOADED
        }
    }

    /**
     * Устанавливает текущую дату, которая отображается в pager, для
     * ее последующего отображения, если расписание обновится.
     */
    fun updatePagingDate(currentPagingDate: LocalDate?) {
        handle.set(CURRENT_PAGER_DATE, currentPagingDate)
    }

    /**
     * Переименовывает текущие расписание.
     */
    fun renameSchedule(newScheduleName: String) {
        val currentScheduleItem = _scheduleItem.value ?: return

        viewModelScope.launch(Dispatchers.IO) {
            currentScheduleItem.scheduleName = newScheduleName
            repository.updateScheduleItem(currentScheduleItem)
            WidgetUtils.updateScheduleWidget(getApplication(), currentScheduleItem.id, repository)

            _actionState.emit(ScheduleActionState.RENAMED)
        }
    }

    /**
     * Удаляет расписание с устройства пользователя.
     */
    fun removeSchedule() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.removeSchedule(scheduleId)
            _actionState.emit(ScheduleActionState.REMOVED)
        }
    }

    /**
     * Сохраняет расписание на устройство по заданному пути.
     */
    fun saveScheduleToDevice(uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.saveToDevice(scheduleId, uri, getApplication())
                _actionState.emit(ScheduleActionState.EXPORTED)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Отключает синхронизацию расписания.
     */
    fun disableScheduleSynced() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.toggleScheduleSyncState(scheduleId, false)
        }
    }

    /**
     * Factory для создания ViewModel.
     */
    @AssistedFactory
    interface ScheduleViewFactory {
        fun create(
            scheduleId: Long,
            handle: SavedStateHandle,
            startScheduleDate: LocalDate,
        ): ScheduleViewViewModel
    }

    companion object {
        /**
         * Размер страницы погрузки.
         */
        const val PAGE_SIZE = 20

        private const val CURRENT_PAGER_DATE = "current_pager_date"

        /**
         * Создает объект в Factory c переданными параметрами.
         */
        fun provideFactory(
            factory: ScheduleViewFactory,
            scheduleId: Long,
            startScheduleDate: LocalDate,
            owner: SavedStateRegistryOwner,
            defaultArgs: Bundle? = null,
        ): ViewModelProvider.Factory =
            object : AbstractSavedStateViewModelFactory(owner, defaultArgs) {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel?> create(
                    key: String,
                    modelClass: Class<T>,
                    handle: SavedStateHandle,
                ): T {
                    return factory.create(
                        scheduleId,
                        handle,
                        startScheduleDate
                    ) as T
                }
            }
    }
}