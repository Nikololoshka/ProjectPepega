package com.vereshchagin.nikolay.stankinschedule.modulejournal.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vereshchagin.nikolay.stankinschedule.core.ui.State
import com.vereshchagin.nikolay.stankinschedule.modulejournal.domain.model.SemesterMarks
import com.vereshchagin.nikolay.stankinschedule.modulejournal.domain.model.Student
import com.vereshchagin.nikolay.stankinschedule.modulejournal.domain.usecase.JournalUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JournalViewModel @Inject constructor(
    private val useCase: JournalUseCase,
) : ViewModel() {

    private val _isSignIn = MutableStateFlow(true)
    val isSignIn = _isSignIn.asStateFlow()

    private val _student = MutableStateFlow<State<Student>>(State.loading())
    val student = _student.asStateFlow()

    private val _semesters = mutableMapOf<String, MutableStateFlow<State<SemesterMarks>>>()

    init {
        viewModelScope.launch {
            useCase.student()
                .catch { e ->
                    _student.value = State.failed(e)
                }
                .collect { student ->
                    if (student == null) {
                        _isSignIn.value = false
                    } else {
                        _student.value = State.success(student)
                    }
                }
        }
    }

    private fun updateSemester(
        semester: String,
        state: MutableStateFlow<State<SemesterMarks>>,
        force: Boolean = false,
    ) {
        if (state is State.Failed<*> || force) {
            viewModelScope.launch {
                useCase.semesterMarks(semester)
                    .catch { e ->
                        e.printStackTrace()
                    }
                    .collect { marks ->
                        state.value = State.success(marks)
                    }
            }
        }
    }

    fun updateSemesterMarks(semester: String) {

    }

    fun semesterMarks(semester: String): StateFlow<State<SemesterMarks>> {
        return _semesters.getOrPut(semester) {
            val state = MutableStateFlow<State<SemesterMarks>>(State.loading())
            updateSemester(semester, state, force = true)
            state
        }
    }
}