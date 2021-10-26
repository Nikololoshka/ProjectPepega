package com.vereshchagin.nikolay.stankinschedule.ui.schedule.myschedules

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.vereshchagin.nikolay.stankinschedule.model.schedule.db.ScheduleItem
import com.vereshchagin.nikolay.stankinschedule.repository.ScheduleRepository
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.myschedules.paging.MyScheduleItem
import com.vereshchagin.nikolay.stankinschedule.utils.extensions.swap
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel списка расписаний.
 */
@HiltViewModel
class MySchedulesViewModel @Inject constructor(
    application: Application,
    private val repository: ScheduleRepository,
) : AndroidViewModel(application) {

    enum class ScheduleAction {
        IMPORTED,
        IMPORTED_FAILED,
        REMOVED_SELECTED
    }


    private val _favorite = MutableStateFlow<ScheduleItem?>(null)
    private var _schedules: MutableList<MyScheduleItem> = arrayListOf()
    private val _schedulesChannel = MutableSharedFlow<MutableList<MyScheduleItem>>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_LATEST
    )
    private val _actionState = MutableSharedFlow<ScheduleAction>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_LATEST
    )

    /**
     * Избранное расписание.
     */
    val favorite = _favorite.asStateFlow()

    /**
     * Список с расписаниями.
     */
    val schedules = _schedulesChannel.asSharedFlow()

    /**
     * Состояние действия над расписания.
     */
    val actionState = _actionState.asSharedFlow()


    init {
        viewModelScope.launch(Dispatchers.IO) {
            repository.schedules()
                .map { list ->
                    MutableList(list.size) { index ->
                        MyScheduleItem(list[index])
                    }
                }
                .collect { list ->
                    val favoriteSchedule = repository.favoriteScheduleId
                    updateFavoriteScheduleItem(favoriteSchedule)

                    if (selectedItems() == 0) {
                        _schedules = list
                        _schedulesChannel.emit(_schedules)
                    }
                }
        }
    }

    /**
     * Создает новое расписание.
     */
    fun createSchedule(scheduleName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.createSchedule(scheduleName)
        }
    }

    /**
     * Устанавливает избранное расписание.
     * Возвращает true, если было установлено новое избранное.
     * False если было удалено избранное.
     */
    fun setFavorite(favoriteId: Long): Boolean {
        val isNew = favoriteId != _favorite.value?.id

        val newFavoriteId = if (isNew) favoriteId else ScheduleRepository.NO_SCHEDULE
        repository.favoriteScheduleId = newFavoriteId
        updateFavoriteScheduleItem(newFavoriteId)

        return isNew
    }

    private fun updateFavoriteScheduleItem(favoriteId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            if (favoriteId == ScheduleRepository.NO_SCHEDULE) {
                _favorite.value = null
            } else {
                _favorite.value = repository.scheduleItem(favoriteId).first()
            }
        }
    }

    /**
     * Переставляет расписания в списке.
     */
    fun moveSchedule(fromPosition: Int, toPosition: Int) {
        _schedules.swap(fromPosition, toPosition)
    }

    /**
     * Сохраняет состояния после изменения в ActionMode.
     */
    fun completeActionMode() {
        viewModelScope.launch(Dispatchers.IO) {
            val currentSchedules = _schedules
            currentSchedules.forEachIndexed { index, item -> item.position = index }
            repository.updateScheduleItems(currentSchedules)
        }
    }

    /**
     * Удаляет выбранные расписания.
     */
    fun removeSelected() {
        viewModelScope.launch(Dispatchers.IO) {
            val currentSchedules = _schedules
            val selected = currentSchedules.filter { item -> item.isSelected }
            _schedules.removeAll(selected)
            selected.forEach { item ->
                repository.removeSchedule(item.scheduleName)
            }
            _actionState.emit(ScheduleAction.REMOVED_SELECTED)
        }
    }

    /**
     * Загружает расписание из json.
     */
    fun loadScheduleFromDevice(uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.loadFromDevice(uri, getApplication())
                _actionState.emit(ScheduleAction.IMPORTED)
            } catch (e: Exception) {
                _actionState.emit(ScheduleAction.IMPORTED_FAILED)
            }
        }
    }

    /**
     * Нажат был элемент в ActionMode.
     */
    fun selectItem(position: Int) {
        val item = _schedules[position]
        item.isSelected = !item.isSelected
    }

    fun selectedItems(): Int {
        return _schedules.count { item -> item.isSelected }
    }

    fun clearSelection() {
        _schedules.forEach { item -> item.isSelected = false }
    }
}