package com.vereshchagin.nikolay.stankinschedule.schedule.table.ui

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vereshchagin.nikolay.stankinschedule.core.domain.repository.LoggerAnalytics
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.ScheduleModel
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.usecase.ScheduleUseCase
import com.vereshchagin.nikolay.stankinschedule.schedule.table.domain.model.ScheduleTable
import com.vereshchagin.nikolay.stankinschedule.schedule.table.domain.model.TableConfig
import com.vereshchagin.nikolay.stankinschedule.schedule.table.domain.model.TableMode
import com.vereshchagin.nikolay.stankinschedule.schedule.table.domain.usecase.AndroidTableUseCase
import com.vereshchagin.nikolay.stankinschedule.schedule.table.ui.components.ExportFormat
import com.vereshchagin.nikolay.stankinschedule.schedule.table.ui.components.ExportProgress
import com.vereshchagin.nikolay.stankinschedule.schedule.table.ui.components.ExportType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import org.joda.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ScheduleTableViewModel @Inject constructor(
    private val scheduleUseCase: ScheduleUseCase,
    private val tableUseCase: AndroidTableUseCase,
    private val loggerAnalytics: LoggerAnalytics
) : ViewModel() {

    private val _scheduleName = MutableStateFlow("")
    val scheduleName = _scheduleName.asStateFlow()

    private val _schedule = MutableStateFlow<ScheduleModel?>(null)

    private val _tableConfig = MutableStateFlow(TableConfig.default())
    val tableConfig = _tableConfig.asStateFlow()

    private val _table = MutableStateFlow<ScheduleTable?>(null)
    val table = _table.asStateFlow()


    private val _exportProgress = MutableStateFlow<ExportProgress>(ExportProgress.Nothing)
    val exportProgress = _exportProgress.asStateFlow()

    private var exportJob: Job? = null
    private var saveFormat: ExportFormat? = null


    init {
        loadTable()
    }

    private fun loadTable() {
        viewModelScope.launch {
            _tableConfig.combine(_schedule.filterNotNull()) { t1, t2 -> t1 to t2 }
                .collectLatest { (config, schedule) ->
                    _table.value = null

                    val table = when (config.mode) {
                        TableMode.Full -> ScheduleTable(
                            schedule = schedule
                        )
                        TableMode.Weekly -> ScheduleTable(
                            schedule = schedule,
                            date = LocalDate.now().plusDays(config.page * 7)
                        )
                    }

                    _table.value = table
                }
        }
    }

    fun loadSchedule(scheduleId: Long) {
        viewModelScope.launch {
            scheduleUseCase.scheduleModel(scheduleId)
                .collectLatest { schedule ->
                    _schedule.value = schedule
                    _scheduleName.value = schedule?.info?.scheduleName ?: ""
                }
        }
    }

    fun setConfig(color: Int, longScreenSize: Float, mode: TableMode, pageNumber: Int) {
        _tableConfig.value = TableConfig(color, longScreenSize, mode, pageNumber)
    }

    fun saveSchedule(uri: Uri) {
        val format = saveFormat
        if (format != null) {
            saveSchedule(uri, format)
        }
    }

    fun setSaveFormat(format: ExportFormat) {
        saveFormat = format
    }

    private fun saveSchedule(uri: Uri, format: ExportFormat) {
        val schedule = _schedule.value ?: return
        val config = _tableConfig.value

        exportJob = launchSaveJob(uri, schedule, config, format)
    }

    fun sendSchedule(format: ExportFormat) {
        val schedule = _schedule.value ?: return
        val name = schedule.info.scheduleName.ifEmpty { "null" }
        val config = _tableConfig.value

        exportJob = launchSendJob(name, schedule, config, format)
    }

    fun exportFinished() {
        _exportProgress.value = ExportProgress.Nothing
    }

    fun cancelExport() {
        exportJob?.cancel()
        exportFinished()
    }

    private fun launchSendJob(
        name: String,
        schedule: ScheduleModel,
        config: TableConfig,
        format: ExportFormat
    ): Job = viewModelScope.async {

        _exportProgress.value = ExportProgress.Running

        val exportTask = when (format) {
            ExportFormat.Image -> tableUseCase::createUriForImage
            ExportFormat.Pdf -> tableUseCase::createUriForPdf
        }

        exportTask(name, schedule, config)
            .catch { t ->
                _exportProgress.value = ExportProgress.Error(t)
                loggerAnalytics.recordException(t)
            }
            .collect { result ->
                _exportProgress.value = ExportProgress.Finished(
                    path = result,
                    type = ExportType.Send,
                    format = format
                )
            }
    }

    private fun launchSaveJob(
        uri: Uri,
        schedule: ScheduleModel,
        config: TableConfig,
        format: ExportFormat
    ): Job = viewModelScope.async {

        _exportProgress.value = ExportProgress.Running

        val exportTask = when (format) {
            ExportFormat.Image -> tableUseCase::saveImageTable
            ExportFormat.Pdf -> tableUseCase::savePdfTable
        }

        exportTask(schedule, config, uri)
            .catch { t ->
                _exportProgress.value = ExportProgress.Error(t)
                loggerAnalytics.recordException(t)
            }
            .collect { result ->
                _exportProgress.value = ExportProgress.Finished(
                    path = result,
                    type = ExportType.Save,
                    format = format
                )
            }
    }
}