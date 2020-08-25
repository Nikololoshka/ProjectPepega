package com.vereshchagin.nikolay.stankinschedule.ui.schedule.myschedules

import android.app.Application
import android.util.SparseBooleanArray
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

    val selectedItems = MutableLiveData<SparseBooleanArray>(SparseBooleanArray())
    val adapterData = MutableLiveData<Pair<List<String>, String>>(null)

    init {
        update()
    }

    /**
     * Обновляет список расписаний и избранное.
     */
    private fun update() {
        val schedules = repository.schedules(getApplication())
        val favorite = SchedulePreference.favorite(getApplication())
        adapterData.value = schedules to favorite
    }

    /**
     * Устанавливает избранное расписание.
     */
    fun setFavorite(favorite: String) {
        SchedulePreference.setFavorite(getApplication(), favorite)
        update()
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
    fun loadScheduleFromJson(json: String) {
        repository.loadAndSaveFromJson(getApplication(), json, "test")
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