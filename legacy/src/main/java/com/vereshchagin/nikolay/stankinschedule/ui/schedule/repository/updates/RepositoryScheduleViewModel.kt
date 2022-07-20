package com.vereshchagin.nikolay.stankinschedule.ui.schedule.repository.updates

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.vereshchagin.nikolay.stankinschedule.model.schedule.remote.ScheduleItemEntry
import com.vereshchagin.nikolay.stankinschedule.model.schedule.remote.ScheduleUpdateEntry
import com.vereshchagin.nikolay.stankinschedule.repository.ScheduleRemoteRepository
import com.vereshchagin.nikolay.stankinschedule.repository.ScheduleRepository
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.repository.worker.ScheduleDownloadWorker
import com.vereshchagin.nikolay.stankinschedule.utils.State
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * ViewModel расписания с версиями в удаленном репозитории.
 */
// @HiltViewModel
class RepositoryScheduleViewModel @AssistedInject constructor(
    application: Application,
    private val remoteRepository: ScheduleRemoteRepository,
    private val scheduleRepository: ScheduleRepository,
    @Assisted private val scheduleId: Int,
) : AndroidViewModel(application) {


    enum class DownloadState {
        START,
        EXIST
    }


    private val _downloadState = MutableSharedFlow<Pair<DownloadState, String>>()
    private val _updatesState =
        MutableStateFlow<State<Pair<ScheduleItemEntry, List<ScheduleUpdateEntry>>>>(State.loading())

    /**
     * Статус загрузки расписания.
     */
    val downloadState = _downloadState.asSharedFlow()

    /**
     * Версии расписания.
     */
    val updatesState = _updatesState.asStateFlow()

    init {
        updateScheduleUpdates()

//        viewModelScope.launch {
//            WorkManager.getInstance(getApplication())
//                .getWorkInfosByTagLiveData(ScheduleDownloadWorker.WORKER_TAG)
//                .asFlow()
//                .collect { infoList ->
//                    infoList.forEach { workInfo ->
//                        Log.d("MyLog", "${workInfo.id} - $workInfo")
//                    }
//                }
//        }
    }

    /**
     * Обновляет данные расписания с версиями.
     */
    private fun updateScheduleUpdates() {
        viewModelScope.launch(Dispatchers.IO) {
            remoteRepository.scheduleUpdates(scheduleId)
                .catch { e ->
                    _updatesState.value = State.failed(e)
                }
                .collect { state ->
                    _updatesState.value = state
                }
        }
    }

//    private fun updateScheduleSyncInfo() {
//        viewModelScope.launch(Dispatchers.IO) {
////            scheduleRepository.scheduleItem(scheduleName)
////                .collect {
////                    val isSync = it?.synced ?: false
////                    syncState.postValue(if (isSync) SyncState.SYNCED else SyncState.NOT_SYNCED)
////                }
//        }
//    }

    fun currentScheduleName(): String? {
        val currentState = _updatesState.value
        if (currentState is State.Success) {
            return currentState.data.first.name
        }
        return null
    }

    /**
     * Запускает скачивание расписания.
     */
    fun downloadScheduleUpdate(
        scheduleName: String,
        scheduleEntry: ScheduleUpdateEntry,
        replaceExist: Boolean,
    ) {
        viewModelScope.launch {
            val isExist = scheduleRepository.isScheduleExist(scheduleName)
            if (isExist) {
                _downloadState.emit(DownloadState.EXIST to scheduleName)
            } else {
                ScheduleDownloadWorker.startWorker(
                    getApplication(),
                    scheduleName,
                    replaceExist,
                    scheduleEntry.item,
                    scheduleEntry.updateId
                )
                _downloadState.emit(DownloadState.START to scheduleName)
            }
        }
    }

//    /**
//     * Запускает скачивание расписания.
//     */
//    fun downloadScheduleVersion(
//        saveScheduleName: String,
//        version: ScheduleVersion,
//        replace: Boolean = false,
//    ) {
//        val entry = scheduleEntry.value ?: return
//        if (entry is State.Success) {
//            viewModelScope.launch(Dispatchers.IO) {
//                val data = entry.data
//
//                // существует расписание с таким именем
//                if (!replace && scheduleRepository.isScheduleExist(saveScheduleName)) {
//                    downloadState.postValue(DownloadState.EXIST)
//                } else {
//                    ScheduleDownloadWorker.startWorker(
//                        getApplication(),
//                        saveScheduleName = saveScheduleName,
//                        replaceExist = replace,
//                        scheduleName = data.name,
//                        scheduleId = data.id,
//                        versionName = LocalDate.parse(version.date)
//                            .toString(DateTimeUtils.PRETTY_DATE_PATTERN),
//                        scheduleVersionId = data.versions.indexOfFirst { v -> v.path == version.path },
//                        isSync = false,
//                        data.name, version.path
//                    )
//                    downloadState.postValue(DownloadState.START)
//                }
//            }
//        }
//    }
//
//    /**
//     * Запускает синхронизацию выбранного расписания.
//     */
//    private suspend fun syncSchedule(replace: Boolean) {
//        // существует расписание с таким именем
////        if (!replace && scheduleRepository.isScheduleExist(scheduleName)) {
//            syncState.postValue(SyncState.EXIST)
//        } else {
//            val entry = scheduleEntry.value
//            if (entry is State.Success) {
//                val data = entry.data
//                val version = entry.data.versions.lastOrNull() ?: return
//
//                ScheduleDownloadWorker.startWorker(
//                    getApplication(),
//                    saveScheduleName = scheduleName,
//                    replaceExist = replace,
//                    scheduleName = scheduleName,
//                    scheduleId = data.id,
//                    versionName = LocalDate.parse(version.date)
//                        .toString(DateTimeUtils.PRETTY_DATE_PATTERN),
//                    scheduleVersionId = data.versions.indexOfFirst { v -> v.path == version.path },
//                    isSync = true,
//                    data.name, version.path
//                )
//            }
//        }
//    }
//
//    /**
//     * Переключает состояние синхронизации расписания.
//     */
//    fun toggleSyncSchedule(replace: Boolean) {
//        val currentSyncState = syncState.value
//        viewModelScope.launch(Dispatchers.IO) {
//            // отключение синхронизации
//            if (currentSyncState == SyncState.SYNCED) {
////                scheduleRepository.toggleScheduleSyncState(scheduleName, false)
//            }
//            // включение синхронизации
//            else if (currentSyncState == SyncState.NOT_SYNCED || currentSyncState == SyncState.EXIST) {
//                syncState.postValue(SyncState.SYNCING)
//                syncSchedule(replace)
//            }
//        }
//    }
//
//    /**
//     * Вызывается, когда состояние загрузки расписания в UI обработано.
//     */
//    fun downloadStateComplete() {
//        downloadState.value = DownloadState.IDLE
//    }

    /**
     * Factory для создания ViewModel.
     */
    @AssistedFactory
    interface RepositoryScheduleFactory {
        fun create(scheduleId: Int): RepositoryScheduleViewModel
    }

    companion object {
        /**
         * Создает объект в Factory c переданными параметрами.
         */
        fun provideFactory(
            factory: RepositoryScheduleFactory,
            scheduleId: Int,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return factory.create(scheduleId) as T
            }
        }
    }
}