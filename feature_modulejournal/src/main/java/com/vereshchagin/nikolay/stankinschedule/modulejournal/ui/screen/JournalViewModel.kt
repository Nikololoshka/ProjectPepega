package com.vereshchagin.nikolay.stankinschedule.modulejournal.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vereshchagin.nikolay.stankinschedule.core.ui.State
import com.vereshchagin.nikolay.stankinschedule.modulejournal.domain.model.Student
import com.vereshchagin.nikolay.stankinschedule.modulejournal.domain.usecase.JournalUseCase
import com.vereshchagin.nikolay.stankinschedule.modulejournal.domain.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JournalViewModel @Inject constructor(
    private val useCase: JournalUseCase
) : ViewModel() {

    private val _student = MutableStateFlow<State<Student>>(State.loading())
    val student = _student.asStateFlow()

    init {
        viewModelScope.launch {
            useCase.student().collect {
                _student.value = State.success(it)
            }
        }
    }
}