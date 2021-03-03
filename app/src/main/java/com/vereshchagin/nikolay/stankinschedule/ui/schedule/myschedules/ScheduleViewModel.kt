package com.vereshchagin.nikolay.stankinschedule.ui.schedule.myschedules

import android.app.Application
import android.util.SparseBooleanArray
import androidx.core.util.forEach
import androidx.lifecycle.*
import com.vereshchagin.nikolay.stankinschedule.model.schedule.db.ScheduleItem
import com.vereshchagin.nikolay.stankinschedule.repository.ScheduleRepository
import com.vereshchagin.nikolay.stankinschedule.utils.DifferenceUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*

/**
 * ViewModel списка расписаний.
 */
class ScheduleViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ScheduleRepository(application)

    val favorite = MutableLiveData<String>(null)
    val selectedItems = MutableLiveData(SparseBooleanArray())

    private var currentSchedules: MutableList<ScheduleItem> = arrayListOf()

    val schedules = MutableLiveData<List<ScheduleItem>>(emptyList())

    init {
        favorite.value = ScheduleRepository.favorite(getApplication())
        viewModelScope.launch(Dispatchers.IO) {
            repository.schedules()
                .collect { list ->
                    synchronized(currentSchedules) {
                        val isDiff = DifferenceUtils.applyDifference(currentSchedules, list)
                        if (isDiff) {
                            schedules.postValue(list)
                        }
                    }
                }
        }
    }

    /**
     * Удаляет выбранные расписания.
     */
    fun removeSelected() {
        val currentSchedules = schedules.value

        viewModelScope.launch(Dispatchers.IO) {
            val removingItems = selectedItems.value
            removingItems?.forEach { key, value ->
                if (currentSchedules != null && value) {
                    val item = currentSchedules[key]
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
    fun setFavorite(favorite: String): Boolean {
        val old = ScheduleRepository.favorite(getApplication())
        val isNew = favorite != old

        ScheduleRepository.setFavorite(getApplication(), if (isNew) favorite else null)
        this.favorite.value = favorite

        return isNew
    }

    /**
     * Переставляет расписания в списке.
     */
    fun moveSchedule(fromPosition: Int, toPosition: Int) {
        synchronized(currentSchedules) {
            Collections.swap(currentSchedules, fromPosition, toPosition)
        }
    }

    fun actionModeCompleted() {
        viewModelScope.launch(Dispatchers.IO) {
            synchronized(currentSchedules) {
                currentSchedules.forEachIndexed { index, item -> item.position = index }
            }
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
    fun selectItem(position: Int): Int {
        selectedItems.value?.let {
            if (it.get(position, false)) {
                it.delete(position)
            } else {
                it.put(position, true)
            }
            return it.size()
        }
        return 0
    }

    fun clearSelection() {
        selectedItems.value?.clear()
    }

    /**
     * Factory для создания ScheduleViewModel.
     */
    class Factory(val application: Application) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return ScheduleViewModel(application) as T
        }
    }
}