package com.vereshchagin.nikolay.stankinschedule.ui.modulejournal.predict

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.vereshchagin.nikolay.stankinschedule.model.modulejournal.MarkType
import com.vereshchagin.nikolay.stankinschedule.model.modulejournal.SemesterMarks
import com.vereshchagin.nikolay.stankinschedule.repository.ModuleJournalRepository
import kotlinx.coroutines.launch

/**
 * ViewModel для вычисления рейтинга студента.
 */
class ModuleJournalPredictViewModel(
    private val repository: ModuleJournalRepository,
) : ViewModel() {

    val semester = MutableLiveData<String>(null)
    val semestersArray = MutableLiveData<Array<String>>(null)
    val semesterMarks = MutableLiveData<SemesterMarks>(null)
    val showAllDiscipline = MutableLiveData(false)
    val rating = MutableLiveData(0.0)

    init {
        viewModelScope.launch {
            val studentData = repository.loadStudentData()
            val semesters = studentData.semesters.toTypedArray()
            semestersArray.value = semesters
            updateSemesterMarks(semesters.last())
        }
    }

    fun updateSemesterMarks(semester: String) {
        this.semester.value = semester
        viewModelScope.launch {
            val marks = repository.loadSemesterMarks(semester)
            semesterMarks.value = marks
            rating.value = marks.computeRating()
        }
    }

    fun updateShowDisciplines(showAll: Boolean) {
        showAllDiscipline.value = showAll
    }

    fun updateMark(disciplineName: String, type: MarkType, mark: Int) {
        semesterMarks.value?.let {
            it.updateMark(disciplineName, type, mark)
            rating.value = it.computeRating()
        }
    }

    /**
     * Фабрика для создания ViewModel.
     */
    class Factory(
        private val application: Application,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return ModuleJournalPredictViewModel(
                ModuleJournalRepository(application.cacheDir)
            ) as T
        }
    }
}