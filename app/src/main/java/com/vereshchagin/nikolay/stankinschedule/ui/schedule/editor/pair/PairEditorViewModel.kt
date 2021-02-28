package com.vereshchagin.nikolay.stankinschedule.ui.schedule.editor.pair

import android.app.Application
import androidx.lifecycle.*
import com.vereshchagin.nikolay.stankinschedule.model.schedule.ScheduleKt
import com.vereshchagin.nikolay.stankinschedule.model.schedule.db.PairItem
import com.vereshchagin.nikolay.stankinschedule.model.schedule.pair.Pair
import com.vereshchagin.nikolay.stankinschedule.repository.ScheduleRepositoryKt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * ViewModel для редактирования пар.
 */
class PairEditorViewModel(
    application: Application,
    private val scheduleName: String,
    private val editablePairId: Long,
) : AndroidViewModel(application) {

    private val repository = ScheduleRepositoryKt(application)
    var schedule: ScheduleKt? = null
        get() {
            if (field == null) {
                scheduleState.value = State.ERROR
            }
            return field
        }
        private set

    val editablePair = MutableLiveData<PairItem>(null)

    /**
     * Состояние загрузки расписания.
     */
    val scheduleState = MutableLiveData(State.LOADING)

    init {
        // загрузка расписания
        viewModelScope.launch(Dispatchers.IO) {
            loadEditablePair()
            loadSchedule()
        }
    }

    private suspend fun loadEditablePair() {
        editablePair.postValue(repository.pair(editablePairId).first())
    }

    private suspend fun loadSchedule() {
        schedule = repository.schedule(scheduleName).first()
        scheduleState.postValue(State.SUCCESSFULLY_LOADED)
    }

    fun changePair(newPair: Pair) {
        val editablePair = editablePair.value
        val currentSchedule = schedule

        if (currentSchedule != null) {
            // Проверка на возможность замены пары в UI потоке.
            // Если не получиться заменить то возникнет исключение
            currentSchedule.possibleChangePair(editablePair, newPair)

            viewModelScope.launch(Dispatchers.IO) {
                scheduleState.postValue(State.LOADING)
                repository.updatePair(
                    newPair.toPairItem(
                        currentSchedule.info.id,    // id расписания
                        editablePair?.id ?: 0       // id пары (0 - новая пара)
                    )
                )
                scheduleState.postValue(State.SUCCESSFULLY_SAVED)
            }
        }
    }

    fun removePair() {
        val editablePair = editablePair.value

        // нет пары для удаления
        if (editablePair == null) {
            scheduleState.value = State.SUCCESSFULLY_SAVED
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            scheduleState.postValue(State.LOADING)
            repository.removePair(editablePair)
            scheduleState.postValue(State.SUCCESSFULLY_SAVED)
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
        val application: Application, val scheduleName: String, val editablePairId: Long,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return PairEditorViewModel(application, scheduleName, editablePairId) as T
        }
    }
}