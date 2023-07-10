package com.vereshchagin.nikolay.stankinschedule.schedule.parser.ui

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vereshchagin.nikolay.stankinschedule.core.domain.repository.LoggerAnalytics
import com.vereshchagin.nikolay.stankinschedule.core.domain.usecase.DeviceUseCase
import com.vereshchagin.nikolay.stankinschedule.core.ui.components.UIState
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.ScheduleInfo
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.ScheduleModel
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.usecase.ScheduleUseCase
import com.vereshchagin.nikolay.stankinschedule.schedule.parser.domain.model.ParseResult
import com.vereshchagin.nikolay.stankinschedule.schedule.parser.domain.model.ParserSettings
import com.vereshchagin.nikolay.stankinschedule.schedule.parser.domain.usecase.ParserUseCase
import com.vereshchagin.nikolay.stankinschedule.schedule.parser.ui.model.ParsedFile
import com.vereshchagin.nikolay.stankinschedule.schedule.parser.ui.model.ParserState
import com.vereshchagin.nikolay.stankinschedule.schedule.parser.ui.model.SaveScheduleError
import com.vereshchagin.nikolay.stankinschedule.schedule.parser.ui.model.SelectedFile
import com.vereshchagin.nikolay.stankinschedule.schedule.table.domain.model.ScheduleTable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.joda.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ScheduleParserViewModel @Inject constructor(
    private val deviceUseCase: DeviceUseCase,
    private val parserUseCase: ParserUseCase,
    private val loggerAnalytics: LoggerAnalytics,
    private val scheduleUseCase: ScheduleUseCase
) : ViewModel() {

    private val _parserState = MutableStateFlow<ParserState>(ParserState.SelectFile())
    val parserState = _parserState.asStateFlow()

    // cache
    private var _selectedFile: SelectedFile? = null
    private var _parserSettings: ParserSettings = ParserSettings(
        scheduleYear = LocalDate.now().year,
        parserThreshold = 1f
    )
    private var _parserResult: ParsedFile? = null
    private var _scheduleName: String = ""

    fun selectFile(uri: Uri) {
        viewModelScope.launch {
            try {
                val filename = deviceUseCase.extractFilename(uri.toString())
                    .substringBeforeLast('.')
                val preview = parserUseCase.renderPreview(uri.toString())
                val selectedFile = SelectedFile(uri, filename)

                _parserState.value = ParserState.SelectFile(selectedFile, preview)
                _selectedFile = selectedFile

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun onSetupSettings(settings: ParserSettings) {
        _parserSettings = settings
    }

    fun onScheduleNameChanged(scheduleName: String) {
        _scheduleName = scheduleName
    }

    private fun checkScheduleName(scheduleName: String, currentResult: ParsedFile) {
        viewModelScope.launch {
            if (scheduleName.isEmpty()) {
                _parserState.value = ParserState.SaveResult(
                    scheduleName = scheduleName,
                    saveScheduleError = SaveScheduleError.InvalidScheduleName
                )
                return@launch
            }

            val isExists = scheduleUseCase.isScheduleExists(scheduleName)
            if (isExists) {
                _parserState.value = ParserState.SaveResult(
                    scheduleName = scheduleName,
                    saveScheduleError = SaveScheduleError.ScheduleNameAlreadyExists
                )
                return@launch
            }

            val schedule = createScheduleModel(scheduleName, currentResult.successResult)
            saveSchedule(schedule)
        }
    }

    private fun saveSchedule(schedule: ScheduleModel) {
        viewModelScope.launch {
            _parserState.value = ParserState.ImportFinish(state = UIState.loading())

            scheduleUseCase.createSchedule(schedule)
                .catch { e ->
                    _parserState.value = ParserState.ImportFinish(state = UIState.failed(e))
                }
                .collectLatest { isCreated ->
                    if (isCreated) {
                        _parserState.value = ParserState.ImportFinish(
                            state = UIState.success(Unit)
                        )
                    } else {
                        val error = IllegalArgumentException("Failed to create a schedule")
                        _parserState.value = ParserState.ImportFinish(
                            state = UIState.failed(error)
                        )
                    }
                }
        }
    }

    private fun startScheduleParser(
        selectedFile: SelectedFile,
        settings: ParserSettings
    ) {
        viewModelScope.launch {
            try {
                _parserState.value = ParserState.ParserResult(UIState.loading())

                val result = parserUseCase.parsePDF(selectedFile.path.toString(), settings)

                val successResult = mutableListOf<ParseResult.Success>()
                val missingResult = mutableListOf<ParseResult.Missing>()
                val errorResult = mutableListOf<ParseResult.Error>()
                for (r in result) {
                    when (r) {
                        is ParseResult.Success -> successResult += r
                        is ParseResult.Error -> errorResult += r
                        is ParseResult.Missing -> missingResult += r
                    }
                }

                val scheduleName = selectedFile.filename
                val schedule = createScheduleModel(scheduleName, successResult)

                val parsedFile = ParsedFile(
                    successResult = successResult,
                    missingResult = missingResult,
                    errorResult = errorResult,
                    table = ScheduleTable(schedule)
                )

                _parserResult = parsedFile
                _parserState.value = ParserState.ParserResult(UIState.success(parsedFile))

            } catch (e: Exception) {
                _parserState.value = ParserState.ParserResult(UIState.failed(e))
                loggerAnalytics.recordException(e)
            }
        }
    }

    private fun createScheduleModel(
        scheduleName: String,
        successResult: List<ParseResult.Success>
    ): ScheduleModel {
        val schedule = ScheduleModel(info = ScheduleInfo(scheduleName))
        successResult.forEach {
            try {
                schedule.add(it.pair)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return schedule
    }


    fun back() {
        when (_parserState.value) {
            is ParserState.Settings -> {
                _selectedFile?.let { currentSelectedFile -> selectFile(currentSelectedFile.path) }
            }

            is ParserState.ParserResult -> {
                _parserState.value = ParserState.Settings(_parserSettings)
            }

            is ParserState.SaveResult -> {
                val currentResult = _parserResult
                if (currentResult != null) {
                    _parserState.value = ParserState.ParserResult(UIState.success(currentResult))
                } else {
                    _parserState.value = ParserState.SelectFile()
                }
            }
        }
    }

    fun next() {
        when (_parserState.value) {
            is ParserState.SelectFile -> {
                if (_selectedFile != null) {
                    _parserState.value = ParserState.Settings(_parserSettings)
                }
            }

            is ParserState.Settings -> {
                val currentSelectedFile = _selectedFile
                if (currentSelectedFile != null) {
                    startScheduleParser(currentSelectedFile, _parserSettings)
                }
            }

            is ParserState.ParserResult -> {
                val name = _scheduleName.ifEmpty { _selectedFile?.filename ?: "" }
                _scheduleName = name
                _parserState.value = ParserState.SaveResult(name)
            }

            is ParserState.SaveResult -> {
                val currentResult = _parserResult
                if (currentResult != null) {
                    checkScheduleName(_scheduleName, currentResult)
                } else {
                    _parserState.value = ParserState.SelectFile()
                }
            }
        }
    }
}