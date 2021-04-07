package com.vereshchagin.nikolay.stankinschedule.ui.schedule.myschedules

import android.app.Application
import android.util.SparseBooleanArray
import androidx.core.util.forEach
import androidx.lifecycle.*
import com.vereshchagin.nikolay.stankinschedule.model.schedule.db.ScheduleItem
import com.vereshchagin.nikolay.stankinschedule.repository.ScheduleRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

/**
 * ViewModel списка расписаний.
 */
class MyScheduleViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ScheduleRepository(application)

    val favorite = MutableLiveData<String>(null)
    val selectedItems = MutableLiveData(SparseBooleanArray())

    private var currentSchedules = arrayListOf<ScheduleItem>()
    val schedules = object : MutableLiveData<List<ScheduleItem>>() {
        override fun getValue(): List<ScheduleItem> {
            return currentSchedules
        }
    }

    init {
        favorite.value = ScheduleRepository.favorite(getApplication())
        viewModelScope.launch(Dispatchers.IO) {
            repository.schedules()
                .collect { list ->
                    synchronized(currentSchedules) {
                        val isDiff = compareDifference(currentSchedules, list)
                        if (isDiff) {
                            schedules.postValue(currentSchedules)
                        }
                    }
                }
        }
    }

    private fun compareDifference(
        currentList: MutableList<ScheduleItem>,
        newList: List<ScheduleItem>,
    ): Boolean {
        if (currentList.isEmpty() || newList.isEmpty()) {
            currentList.clear()
            currentList.addAll(newList)
            return true
        }

        if (currentList == newList) {
            return false
        }

        val newItems = newList.filter {
            for (item in currentList) {
                if (it.id == item.id) {
                    return@filter false
                }
            }
            return@filter true
        }
        currentList.addAll(newItems)

        return true
    }

    /**
     * Удаляет выбранные расписания.
     */
    fun removeSelected() {
        viewModelScope.launch(Dispatchers.IO) {
            val schedules = ArrayList(currentSchedules)
            val removingItems = selectedItems.value
            removingItems?.forEach { key, value ->
                if (value) {
                    val item = schedules[key]
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
            return MyScheduleViewModel(application) as T
        }
    }
}