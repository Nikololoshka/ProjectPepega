package com.vereshchagin.nikolay.stankinschedule.ui.modulejournal.predict

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.vereshchagin.nikolay.stankinschedule.model.modulejournal.MarkType
import com.vereshchagin.nikolay.stankinschedule.model.modulejournal.SemesterMarks
import com.vereshchagin.nikolay.stankinschedule.repository.ModuleJournalRepository
import com.vereshchagin.nikolay.stankinschedule.utils.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * ViewModel для вычисления рейтинга студента.
 */
class ModuleJournalPredictViewModel(
    private val repository: ModuleJournalRepository,
) : ViewModel() {

    val semester = MutableLiveData<String>(null)
    val semestersArray = MutableLiveData<Array<String>>(null)
    val semesterMarks = MutableLiveData<State<SemesterMarks>>(State.loading())
    val showAllDiscipline = MutableLiveData(false)
    val rating = MutableLiveData(0.0)

    init {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val studentData = repository.loadStudentData()
                val semesters = studentData.semesters.toTypedArray()
                semestersArray.postValue(semesters)
                updateSemesterMarks(semesters.last())

            } catch (e: Exception) {
                semesterMarks.postValue(State.failed(e))
            }
        }
    }

    fun refreshSemesterMarks(semester: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                updateSemesterMarks(semester)
            } catch (e: Exception) {
                semesterMarks.postValue(State.failed(e))
            }
        }
    }

    private suspend fun updateSemesterMarks(semester: String) {
        this.semester.postValue(semester)
        val marks = repository.loadSemesterMarks(semester)
        semesterMarks.postValue(State.success(marks))
        rating.postValue(marks.computeRating())
    }

    fun updateShowDisciplines(showAll: Boolean) {
        showAllDiscipline.value = showAll
    }

    fun updateMark(disciplineName: String, type: MarkType, mark: Int) {
        val marks = semesterMarks.value
        if (marks is State.Success) {
            marks.data.updateMark(disciplineName, type, mark)
            rating.value = marks.data.computeRating()
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