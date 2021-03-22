package com.vereshchagin.nikolay.stankinschedule.ui.schedule.repository

import android.app.Application
import androidx.lifecycle.*
import com.vereshchagin.nikolay.stankinschedule.model.schedule.repository.v1.ScheduleEntry
import com.vereshchagin.nikolay.stankinschedule.model.schedule.repository.v1.ScheduleVersionEntry
import com.vereshchagin.nikolay.stankinschedule.repository.ScheduleRemoteRepository
import com.vereshchagin.nikolay.stankinschedule.repository.ScheduleRepository
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.repository.worker.ScheduleDownloadWorker
import com.vereshchagin.nikolay.stankinschedule.utils.State
import com.vereshchagin.nikolay.stankinschedule.utils.extensions.toPrettyDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 *
 */
class RepositoryScheduleViewModel(
    application: Application,
    private val scheduleId: Int,
) : AndroidViewModel(application) {

    private val repository = ScheduleRemoteRepository(application)

    /**
     *
     */
    val scheduleEntry = MutableLiveData<State<ScheduleEntry>>(State.loading())

    init {
        updateScheduleEntry()
    }

    /**
     *
     */
    private fun updateScheduleEntry() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getScheduleEntry(scheduleId)
                .catch { e ->
                    scheduleEntry.postValue(State.failed(e))
                }
                .collect { entry ->
                    scheduleEntry.postValue(State.success(entry))
                }
        }
    }

    fun downloadScheduleVersion(version: ScheduleVersionEntry) {
        val entry = scheduleEntry.value ?: return
        if (entry is State.Success) {
            viewModelScope.launch(Dispatchers.IO) {
                val data = entry.data

                // существует расписание с таким именем
                val scheduleRepository = ScheduleRepository(getApplication())
                if (scheduleRepository.isScheduleExist(data.name)) {
                    // ...
                } else {
                    ScheduleDownloadWorker.startWorker(
                        getApplication(),
                        data.name,
                        data.id,
                        version.date.toPrettyDate(),
                        data.versions.indexOfFirst { v -> v.path == version.path },
                        data.name, version.path
                    )
                }
            }
        }
    }

    class Factory(
        private val application: Application,
        private val scheduleId: Int,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return RepositoryScheduleViewModel(application, scheduleId) as T
        }
    }
}