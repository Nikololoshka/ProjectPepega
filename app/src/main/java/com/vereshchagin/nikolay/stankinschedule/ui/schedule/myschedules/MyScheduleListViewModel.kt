package com.vereshchagin.nikolay.stankinschedule.ui.schedule.myschedules

import android.app.Application
import android.util.SparseBooleanArray
import androidx.core.util.forEach
import androidx.lifecycle.*
import com.vereshchagin.nikolay.stankinschedule.model.schedule.db.ScheduleItem
import com.vereshchagin.nikolay.stankinschedule.model.schedule.review.MyScheduleListItem
import com.vereshchagin.nikolay.stankinschedule.repository.ScheduleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

/**
 * ViewModel списка расписаний.
 */
@HiltViewModel
class MyScheduleListViewModel @Inject constructor(
    application: Application,
    private val repository: ScheduleRepository,
) : AndroidViewModel(application) {

    private val _selectedItems = MutableStateFlow(SparseBooleanArray())
    private val _schedules = MutableStateFlow<List<ScheduleItem>>(emptyList())
    private val _favorite = MutableStateFlow<String?>(null)

    val favorite = _favorite.asStateFlow()
    val selectedItems = _selectedItems.asStateFlow()

    val myScheduleList =
        combine(_schedules, _selectedItems) { schedules, selected ->
            schedules to selected
        }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _favorite.value = ScheduleRepository.favorite(application)
            repository.schedules()
                .collect { list ->
                    _schedules.value = list
                }
        }
    }

    /**
     * Удаляет выбранные расписания.
     */
    fun removeSelected() {
        viewModelScope.launch(Dispatchers.IO) {
            val currentSchedules = _schedules.value
            val removingItems = _selectedItems.value
            removingItems.forEach { index, isRemove ->
                if (isRemove) {
                    val item = currentSchedules[index]
                    repository.removeSchedule(item.scheduleName)
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
    fun setFavorite(newFavorite: String): Boolean {
        val old = ScheduleRepository.favorite(getApplication())
        val isNew = newFavorite != old

        ScheduleRepository.setFavorite(getApplication(), if (isNew) newFavorite else null)
        this._favorite.value = newFavorite

        return isNew
    }

    /**
     * Переставляет расписания в списке.
     */
    fun moveSchedule(fromPosition: Int, toPosition: Int) {
        Collections.swap(_schedules.value, fromPosition, toPosition)
    }

    fun actionModeCompleted() {
        viewModelScope.launch(Dispatchers.IO) {
            val currentSchedules = _schedules.value
            currentSchedules.forEachIndexed { index, item -> item.position = index }
            repository.updateScheduleItems(currentSchedules)
        }
    }

    /**
     * Загружает расписание из json.
     */
    fun loadScheduleFromJson(json: String, scheduleName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveResponse(scheduleName, json)
        }
    }

    /**
     * Нажат был элемент в ActionMode.
     */
    fun selectItem(position: Int) {
        val selected = _selectedItems.value
        if (selected.get(position, false)) {
            selected.delete(position)
        } else {
            selected.put(position, true)
        }
        _selectedItems.value = selected
    }

    fun clearSelection() {
        val selected = _selectedItems.value
        selected.clear()
        _selectedItems.value = selected
    }
}