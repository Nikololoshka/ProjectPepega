package com.vereshchagin.nikolay.stankinschedule.schedule.creator.ui

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vereshchagin.nikolay.stankinschedule.schedule.creator.domain.usecase.ScheduleCreatorUseCase
import com.vereshchagin.nikolay.stankinschedule.schedule.creator.ui.components.CreateEvent
import com.vereshchagin.nikolay.stankinschedule.schedule.creator.ui.components.CreateState
import com.vereshchagin.nikolay.stankinschedule.schedule.creator.ui.components.ImportState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScheduleCreatorViewModel @Inject constructor(
    private val useCase: ScheduleCreatorUseCase
) : ViewModel() {

    private val _createState = MutableStateFlow<CreateState?>(null)
    val createState = _createState.asStateFlow()

    private val _importState = MutableStateFlow<ImportState?>(null)
    val importState = _importState.asStateFlow()

    fun onCreateSchedule(event: CreateEvent) {
        _createState.value = when (event) {
            CreateEvent.Cancel -> null
            CreateEvent.New -> CreateState.New
        }
    }

    fun createSchedule(scheduleName: String) {
        viewModelScope.launch {
            useCase.createEmptySchedule(scheduleName)
                .catch { e ->
                    _createState.value = CreateState.Error(e)
                }
                .collect { isCreated ->
                    _createState.value = if (isCreated) {
                        CreateState.Success
                    } else {
                        CreateState.AlreadyExist()
                    }
                }
        }
    }

    fun importSchedule(context: Context, uri: Uri) {
        viewModelScope.launch {
            useCase.importSchedule(context, uri)
                .catch { e ->
                    _importState.value = ImportState.Failed(e)
                }
                .collect {
                    _importState.value = ImportState.Success(it)
                }
        }
    }
}