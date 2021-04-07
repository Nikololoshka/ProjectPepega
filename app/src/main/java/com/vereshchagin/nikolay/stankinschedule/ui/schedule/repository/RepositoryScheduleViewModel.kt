package com.vereshchagin.nikolay.stankinschedule.ui.schedule.repository

import android.app.Application
import androidx.lifecycle.*
import com.vereshchagin.nikolay.stankinschedule.model.schedule.repository.v1.ScheduleEntry
import com.vereshchagin.nikolay.stankinschedule.model.schedule.repository.v1.ScheduleVersion
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
 * ViewModel расписания с версиями в удаленном репозитории.
 */
class RepositoryScheduleViewModel(
    application: Application,
    private val scheduleName: String,
    private val scheduleId: Int,
) : AndroidViewModel(application) {

    enum class SyncState {
        SYNCED,
        SYNCING,
        NOT_SYNCED,
        EXIST
    }

    enum class DownloadState {
        START,
        EXIST,
        IDLE
    }

    /**
     * Удаленный репозиторий.
     */
    private val remoteRepository = ScheduleRemoteRepository(application)

    /**
     * Локальный репозиторий с расписаниями.
     */
    private val scheduleRepository = ScheduleRepository(application)

    /**
     * Расписание с версиями.
     */
    val scheduleEntry = MutableLiveData<State<ScheduleEntry>>(State.loading())

    /**
     * Статус синхронизации расписания.
     */
    val syncState = MutableLiveData(SyncState.NOT_SYNCED)

    /**
     * Статус загрузки расписания.
     */
    val downloadState = MutableLiveData(DownloadState.IDLE)

    init {
        updateScheduleEntry()
        updateScheduleSyncInfo()
    }

    /**
     * Обновляет данные расписания с версиями.
     */
    private fun updateScheduleEntry() {
        viewModelScope.launch(Dispatchers.IO) {
            remoteRepository.getScheduleEntry(scheduleId)
                .catch { e ->
                    scheduleEntry.postValue(State.failed(e))
                }
                .collect { entry ->
                    scheduleEntry.postValue(State.success(entry))
                }
        }
    }

    private fun updateScheduleSyncInfo() {
        viewModelScope.launch(Dispatchers.IO) {
            scheduleRepository.scheduleItem(scheduleName)
                .collect {
                    val isSync = it?.synced ?: false
                    syncState.postValue(if (isSync) SyncState.SYNCED else SyncState.NOT_SYNCED)
                }
        }
    }

    /**
     * Запускает скачивание расписания.
     */
    fun downloadScheduleVersion(
        saveScheduleName: String,
        position: Int,
        replace: Boolean = false
    ) {
        val entry = scheduleEntry.value ?: return
        if (entry is State.Success) {
            val version = entry.data.versions.getOrNull(position)
            if (version != null) {
                downloadScheduleVersion(saveScheduleName, version, replace)
            }
        }
    }

    /**
     * Запускает скачивание расписания.
     */
    fun downloadScheduleVersion(
        saveScheduleName: String,
        version: ScheduleVersion,
        replace: Boolean = false,
    ) {
        val entry = scheduleEntry.value ?: return
        if (entry is State.Success) {
            viewModelScope.launch(Dispatchers.IO) {
                val data = entry.data

                // существует расписание с таким именем
                if (!replace && scheduleRepository.isScheduleExist(saveScheduleName)) {
                    downloadState.postValue(DownloadState.EXIST)
                } else {
                    ScheduleDownloadWorker.startWorker(
                        getApplication(),
                        saveScheduleName = saveScheduleName,
                        replaceExist = replace,
                        scheduleName = data.name,
                        scheduleId = data.id,
                        versionName = version.date.toPrettyDate(),
                        scheduleVersionId = data.versions.indexOfFirst { v -> v.path == version.path },
                        isSync = false,
                        data.name, version.path
                    )
                    downloadState.postValue(DownloadState.START)
                }
            }
        }
    }

    /**
     * Запускает синхронизацию выбранного расписания.
     */
    private suspend fun syncSchedule(replace: Boolean) {
        // существует расписание с таким именем
        if (!replace && scheduleRepository.isScheduleExist(scheduleName)) {
            syncState.postValue(SyncState.EXIST)
        } else {
            val entry = scheduleEntry.value
            if (entry is State.Success) {
                val data = entry.data
                val version = entry.data.versions.lastOrNull() ?: return

                ScheduleDownloadWorker.startWorker(
                    getApplication(),
                    saveScheduleName = scheduleName,
                    replaceExist = replace,
                    scheduleName = scheduleName,
                    scheduleId = data.id,
                    versionName = version.date.toPrettyDate(),
                    scheduleVersionId = data.versions.indexOfFirst { v -> v.path == version.path },
                    isSync = true,
                    data.name, version.path
                )
            }
        }
    }

    /**
     * Переключает состояние синхронизации расписания.
     */
    fun toggleSyncSchedule(replace: Boolean) {
        val currentSyncState = syncState.value
        viewModelScope.launch(Dispatchers.IO) {
            // отключение синхронизации
            if (currentSyncState == SyncState.SYNCED) {
                scheduleRepository.toggleScheduleSyncState(scheduleName, false)
            }
            // включение синхронизации
            else if (currentSyncState == SyncState.NOT_SYNCED || currentSyncState == SyncState.EXIST) {
                syncState.postValue(SyncState.SYNCING)
                syncSchedule(replace)
            }
        }
    }

    /**
     * Вызывается, когда состояние загрузки расписания в UI обработано.
     */
    fun downloadStateComplete() {
        downloadState.value = DownloadState.IDLE
    }

    /**
     * Фабрика для создания ViewModel.
     */
    class Factory(
        private val application: Application,
        private val scheduleName: String,
        private val scheduleId: Int,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return RepositoryScheduleViewModel(application, scheduleName, scheduleId) as T
        }
    }
}