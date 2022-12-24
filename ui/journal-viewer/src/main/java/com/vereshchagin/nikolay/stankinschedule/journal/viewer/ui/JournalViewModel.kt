package com.vereshchagin.nikolay.stankinschedule.journal.viewer.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.vereshchagin.nikolay.stankinschedule.core.ui.components.UIState
import com.vereshchagin.nikolay.stankinschedule.journal.core.domain.model.SemesterMarks
import com.vereshchagin.nikolay.stankinschedule.journal.core.domain.model.Student
import com.vereshchagin.nikolay.stankinschedule.journal.core.domain.usecase.JournalUseCase
import com.vereshchagin.nikolay.stankinschedule.journal.core.domain.usecase.LoginUseCase
import com.vereshchagin.nikolay.stankinschedule.journal.core.domain.usecase.PredictUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JournalViewModel @Inject constructor(
    private val journal: JournalUseCase,
    private val login: LoginUseCase,
    private val predict: PredictUseCase
) : ViewModel() {

    private val _isSignIn = MutableStateFlow(true)
    val isSignIn = _isSignIn.asStateFlow()

    val isNotification = journal.isUpdateMarksAllow()

    private val _student = MutableStateFlow<UIState<Student>>(UIState.loading())
    val student = _student.asStateFlow()

    private val _rating = MutableStateFlow<String?>(null)
    val rating = _rating.asStateFlow()

    private val _predictedRating = MutableStateFlow<String?>(null)
    val predictedRating = _predictedRating.asStateFlow()

    private val _isForceRefreshing = MutableStateFlow(false)
    val isForceRefreshing = _isForceRefreshing.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val semesters: Flow<PagingData<SemesterMarks>> = _student.flatMapLatest { state ->
        if (state is UIState.Success) createPager(state.data).flow else emptyFlow()
    }.flowOn(Dispatchers.IO).cachedIn(viewModelScope)


    init {
        updateStudentInfo()
    }

    private fun setupStudent(student: Student) {
        _student.value = UIState.success(student)
        _isForceRefreshing.value = false

        updateRating(student)
        updatePredictRating(student)
    }

    private fun createPager(data: Student): Pager<String, SemesterMarks> {
        return Pager(
            config = PagingConfig(pageSize = 1),
            initialKey = data.semesters.first(),
            pagingSourceFactory = { journal.semesterSource(data) }
        )
    }

    private fun updateStudentInfo(useCache: Boolean = true) {
        viewModelScope.launch {
            journal.student(useCache = useCache)
                .catch { e ->
                    _student.value = UIState.failed(e)
                }
                .collect { student ->
                    if (student == null) {
                        _isSignIn.value = false
                    } else {
                        setupStudent(student)
                    }
                }
        }
    }

    private fun updateRating(student: Student) {
        viewModelScope.launch {
            predict.rating(student)
                .catch {
                    _rating.value = null
                }
                .collect { currentRating ->
                    _rating.value = currentRating
                }
        }
    }

    private fun updatePredictRating(student: Student) {
        viewModelScope.launch {
            predict.predictRating(student)
                .catch {
                    _predictedRating.value = null
                }
                .collect { currentRating ->
                    _predictedRating.value = currentRating
                }
        }
    }

    fun refreshStudentInfo(useCache: Boolean) {
        if (_student.value !is UIState.Loading) {

            _isForceRefreshing.value = true
            if (useCache) {
                _student.value = UIState.loading()
            }

            updateStudentInfo(useCache = useCache)
        }
    }

    fun setUpdateMarksAllow(allow: Boolean) {
        viewModelScope.launch {
            journal.setUpdateMarksAllow(allow)
        }
    }

    fun signOut() {
        viewModelScope.launch {
            try {
                login.signOut()
            } catch (ignored: Exception) {

            }
            _isSignIn.value = false
        }
    }
}