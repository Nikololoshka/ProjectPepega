package com.vereshchagin.nikolay.stankinschedule.journal.predict.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vereshchagin.nikolay.stankinschedule.journal.core.domain.model.Discipline
import com.vereshchagin.nikolay.stankinschedule.journal.core.domain.model.SemesterMarks
import com.vereshchagin.nikolay.stankinschedule.journal.core.domain.usecase.PredictUseCase
import com.vereshchagin.nikolay.stankinschedule.journal.predict.ui.model.PredictMark
import com.vereshchagin.nikolay.stankinschedule.journal.predict.ui.utils.combineState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PredictViewModel @Inject constructor(
    private val useCase: PredictUseCase,
) : ViewModel() {

    private val _semesters = MutableStateFlow<List<String>>(emptyList())
    val semesters = _semesters.asStateFlow()

    private val _currentSemester = MutableStateFlow("")
    val currentSemester = _currentSemester.asStateFlow()

    private val _predictMarks = MutableStateFlow<Map<String, List<PredictMark>>>(emptyMap())
    val predictMarks = _predictMarks.asStateFlow()

    private val _showExposedMarks = MutableStateFlow(true)
    val showExposedMarks = _showExposedMarks.asStateFlow()

    private val _predictedRating = MutableStateFlow(0f)
    val predictedRating = _predictedRating.asStateFlow()

    val panelState = combineState(
        predictedRating,
        showExposedMarks,
        scope = viewModelScope,
        transform = { f, s -> f to s }
    )

    private var semesterMarks: SemesterMarks? = null

    init {
        viewModelScope.launch {
            val semesters = useCase.semesters().first()
            _semesters.value = semesters
            changeSemester(semesters.first())
        }
    }

    fun toggleShowExposedMarks() {
        _showExposedMarks.value = !_showExposedMarks.value
    }

    private fun createContent(marks: SemesterMarks): Map<String, List<PredictMark>> {
        return marks.flatMap { discipline ->
            discipline.map { (type, value) ->
                PredictMark(
                    discipline = discipline.title,
                    type = type,
                    isExposed = value != Discipline.NO_MARK,
                    value = value
                )
            }
        }.groupBy { it.discipline }
    }

    private fun updatePredictRating() {
        val marks = semesterMarks ?: return
        _predictedRating.value = useCase.predictSemester(marks).toFloat()
    }

    fun updatePredictMark(item: PredictMark, value: Int) {
        val marks = semesterMarks
        if (marks != null) {
            marks.updateMark(item.discipline, item.type, value)
            updatePredictRating()

            // Compose: _predictMarks.value = createContent(marks)

            // For views:
            _predictMarks.value[item.discipline]?.find { it.type == item.type }?.value = value
        }
    }

    fun changeSemester(semester: String) {
        viewModelScope.launch {
            _currentSemester.value = semester
            val newMarks = useCase.semesterMarks(semester).first()
            semesterMarks = newMarks
            updatePredictRating()

            _predictMarks.value = createContent(newMarks)
        }
    }
}