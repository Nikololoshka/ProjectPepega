package com.vereshchagin.nikolay.stankinschedule.ui.schedule.myschedules

import android.app.Application
import android.util.Log
import android.util.SparseBooleanArray
import androidx.core.util.forEach
import androidx.lifecycle.*
import com.vereshchagin.nikolay.stankinschedule.model.schedule.db.ScheduleItem
import com.vereshchagin.nikolay.stankinschedule.repository.ScheduleRepositoryKt
import com.vereshchagin.nikolay.stankinschedule.settings.SchedulePreference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * ViewModel списка расписаний.
 */
class ScheduleViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ScheduleRepositoryKt(application)

    val favorite = MutableLiveData<String>(null)
    val selectedItems = MutableLiveData(SparseBooleanArray())
    val schedules = MutableLiveData<List<ScheduleItem>>(null)

    init {
        update()
    }

    /**
     * Обновляет список расписаний и избранное.
     */
    fun update() {
        favorite.value = ScheduleRepositoryKt.favorite(getApplication())
        viewModelScope.launch(Dispatchers.IO) {
            repository.schedules()
                .collect { list ->
                    schedules.postValue(list)
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
        val old = ScheduleRepositoryKt.favorite(getApplication())
        val isNew = favorite != old

        ScheduleRepositoryKt.setFavorite(getApplication(), if (isNew) favorite else null)
        this.favorite.value = favorite

        return isNew
    }

    /**
     * Переставляет расписания в списке.
     */
    fun moveSchedule(fromPosition: Int, toPosition: Int) {
        Log.d("MyLog", "moveSchedule: $fromPosition - $toPosition")
        TODO("")
        SchedulePreference.move(getApplication(), fromPosition, toPosition)
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