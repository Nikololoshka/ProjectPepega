package com.vereshchagin.nikolay.stankinschedule.ui.schedule.myschedules

import android.app.Application
import android.util.SparseBooleanArray
import androidx.core.util.forEach
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vereshchagin.nikolay.stankinschedule.repository.ScheduleRepository
import com.vereshchagin.nikolay.stankinschedule.ui.settings.SchedulePreference

/**
 * ViewModel списка расписаний.
 */
class ScheduleViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ScheduleRepository()

    val selectedItems = MutableLiveData(SparseBooleanArray())
    val adapterData = MutableLiveData<Pair<List<String>, String>>(null)

    init {
        update()
    }

    /**
     * Обновляет список расписаний и избранное.
     */
    fun update() {
        val schedules = repository.schedules(getApplication())
        val favorite = SchedulePreference.favorite(getApplication())
        adapterData.value = schedules to favorite
    }

    /**
     * Удаляет выбранные расписания.
     */
    fun removeSelected() {
        val schedules = adapterData.value?.first
        selectedItems.value?.forEach { key, value ->
            if (schedules != null && value) {
                val scheduleName = schedules[key]
                repository.removeSchedule(getApplication(), scheduleName)
            }
        }
        update()
    }

    /**
     * Создает новое расписание.
     */
    fun createSchedule(scheduleName: String) {
        repository.createSchedule(getApplication(), scheduleName)
        update()
    }

    /**
     * Устанавливает избранное расписание.
     * Возвращает true, если было установлено новое избрано.
     * False если было удалено избранное.
     */
    fun setFavorite(favorite: String): Boolean {
        val old = SchedulePreference.favorite(getApplication())
        val isNew = favorite != old

        SchedulePreference.setFavorite(getApplication(), if (isNew) favorite else "")
        update()

        return isNew
    }

    /**
     * Переставляет расписания в списке.
     */
    fun moveSchedule(fromPosition: Int, toPosition: Int) {
        SchedulePreference.move(getApplication(), fromPosition, toPosition)
    }

    /**
     * Загружает расписание из json.
     */
    fun loadScheduleFromJson(json: String, scheduleName: String) {
        repository.loadAndSaveFromJson(getApplication(), json, scheduleName)
        update()
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