package com.vereshchagin.nikolay.stankinschedule.ui.schedule.editor.pair

import android.app.Application
import androidx.lifecycle.*
import com.google.gson.JsonParseException
import com.vereshchagin.nikolay.stankinschedule.model.schedule.Schedule
import com.vereshchagin.nikolay.stankinschedule.model.schedule.pair.DateException
import com.vereshchagin.nikolay.stankinschedule.repository.ScheduleRepository
import com.vereshchagin.nikolay.stankinschedule.ui.settings.SchedulePreference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * ViewModel для редактирование пар.
 */
class PairEditorViewModel(
    application: Application, private val scheduleName: String
) : AndroidViewModel(application) {

    private val repository = ScheduleRepository()
    var schedule: Schedule? = null
        get() {
            if (field == null) {
                scheduleState.value = State.ERROR
            }
            return field
        }

    /**
     * Состояние загрузки расписания.
     */
    val scheduleState = MutableLiveData<State>(State.LOADING)

    init {
        // загрухка расписания
        viewModelScope.launch(Dispatchers.IO) {
            val path = SchedulePreference.createPath(application, scheduleName)
            try {
                schedule = repository.load(path)
                scheduleState.postValue(State.SUCCESSFULLY_LOADED)

            } catch (e: Exception) {
                if (e is JsonParseException || e is DateException) {
                    scheduleState.postValue(State.ERROR)
                } else {
                    throw e
                }
            }
        }
    }

    /**
     * Сохраняет расписание.
     */
    fun saveSchedule() {
        if (schedule == null) {
            scheduleState.value = State.ERROR
            return
        }

        scheduleState.value = State.LOADING
        viewModelScope.launch(Dispatchers.IO) {
            val path = SchedulePreference.createPath(getApplication(), scheduleName)
            try {
                repository.save(schedule!!, path)
                scheduleState.postValue(State.SUCCESSFULLY_SAVED)

            } catch (e: Exception) {
                if (e is JsonParseException || e is DateException) {
                    scheduleState.postValue(State.ERROR)
                } else {
                    throw e
                }
            }
        }

    }

    /**
     * Перечисление состояний.
     */
    enum class State {
        SUCCESSFULLY_SAVED,
        SUCCESSFULLY_LOADED,
        LOADING,
        ERROR
    }

    class Factory(
        val application: Application, val scheduleName: String
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return PairEditorViewModel(application, scheduleName) as T
        }
    }
}