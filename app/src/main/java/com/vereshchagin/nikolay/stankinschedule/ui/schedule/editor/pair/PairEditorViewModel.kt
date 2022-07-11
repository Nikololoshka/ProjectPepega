package com.vereshchagin.nikolay.stankinschedule.ui.schedule.editor.pair

import android.app.Application
import androidx.lifecycle.*
import com.vereshchagin.nikolay.stankinschedule.model.schedule.Schedule
import com.vereshchagin.nikolay.stankinschedule.model.schedule.db.PairItem
import com.vereshchagin.nikolay.stankinschedule.repository.ScheduleRepository
import com.vereshchagin.nikolay.stankinschedule.utils.WidgetUtils
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * ViewModel для редактирования пар.
 */
// @HiltViewModel
class PairEditorViewModel @AssistedInject constructor(
    application: Application,
    private val repository: ScheduleRepository,
    @Assisted("scheduleId") private val scheduleId: Long,
    @Assisted("editablePairId") private val editablePairId: Long,
) : AndroidViewModel(application) {

    var schedule: Schedule? = null
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
        schedule = repository.schedule(scheduleId).first()
        scheduleState.postValue(State.SUCCESSFULLY_LOADED)
    }

    fun changePair(newPair: PairItem) {
        val editablePair = editablePair.value
        val currentSchedule = schedule

        if (currentSchedule != null) {
            // Проверка на возможность замены пары в UI потоке.
            // Если не получиться заменить то возникнет исключение
            currentSchedule.possibleChangePair(editablePair, newPair)

            viewModelScope.launch(Dispatchers.IO) {
                scheduleState.postValue(State.LOADING)

                repository.updatePair(
                    PairItem(
                        currentSchedule.info.id,    // id расписания
                        newPair                     // id пары (0 - новая пара)
                    )
                )
                WidgetUtils.updateScheduleWidgetList(getApplication(), scheduleId)

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
            WidgetUtils.updateScheduleWidgetList(getApplication(), scheduleId)

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

    /**
     * Factory для создания ViewModel.
     */
    @AssistedFactory
    interface PairEditorFactory {
        fun create(
            @Assisted("scheduleId") scheduleId: Long,
            @Assisted("editablePairId") editablePairId: Long,
        ): PairEditorViewModel
    }

    companion object {
        /**
         * Создает объект в Factory c переданными параметрами.
         */
        fun provideFactory(
            factory: PairEditorFactory,
            scheduleId: Long,
            editablePairId: Long,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return factory.create(scheduleId, editablePairId) as T
            }
        }
    }
}