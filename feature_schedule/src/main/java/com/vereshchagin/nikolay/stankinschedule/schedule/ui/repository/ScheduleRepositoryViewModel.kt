package com.vereshchagin.nikolay.stankinschedule.schedule.ui.repository

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vereshchagin.nikolay.stankinschedule.core.ui.State
import com.vereshchagin.nikolay.stankinschedule.schedule.domain.model.*
import com.vereshchagin.nikolay.stankinschedule.schedule.domain.usecase.RepositoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScheduleRepositoryViewModel @Inject constructor(
    private val useCase: RepositoryUseCase,
) : ViewModel() {

    private val _description = MutableStateFlow<State<RepositoryDescription>>(State.loading())
    val description = _description.asStateFlow()

    private var _repositoryItemsCache: List<RepositoryItem>? = null
    private val _repositoryItems = MutableStateFlow<State<List<RepositoryItem>>>(State.loading())
    val repositoryItems = _repositoryItems.asStateFlow()

    private val _category = MutableStateFlow<RepositoryCategory?>(null)
    val category = _category.asStateFlow()

    private val _grade = MutableStateFlow<Grade?>(null)
    val grade = _grade.asStateFlow()

    private val _course = MutableStateFlow<Course?>(null)
    val course = _course.asStateFlow()

    init {
        reloadDescription()
    }

    private fun loadCategory(category: RepositoryCategory) {
        _repositoryItems.value = State.loading()

        viewModelScope.launch {
            useCase.repositoryItems(category.name)
                .catch { e ->
                    _repositoryItems.value = State.failed(e)
                }
                .collect {
                    _repositoryItemsCache = it
                    updateCategoryFilters()
                }
        }
    }

    private fun gradeFilter(item: RepositoryItem, currentGrade: Grade?): Boolean {
        if (currentGrade == null) {
            return true
        }

        val part = item.name.split('-').getOrNull(0) ?: return true
        val itemGrade = when (part.last().uppercaseChar()) {
            'Б' -> Grade.Bachelor
            'М' -> Grade.Magistracy
            'С' -> Grade.Specialist
            'П' -> Grade.Postgraduate
            else -> null
        } ?: return true

        return itemGrade == currentGrade
    }

    fun reloadCategory() {
        val currentCategory = _category.value ?: return
        loadCategory(currentCategory)
    }

    fun reloadDescription() {
        _description.value = State.loading()

        viewModelScope.launch {
            useCase.repositoryDescription()
                .catch { e ->
                    _description.value = State.failed(e)
                }
                .collect { description ->
                    val item = description.categories.first()
                    _category.value = item
                    _description.value = State.success(description)

                    loadCategory(item)
                }
        }
    }

    fun updateGrade(grade: Grade) {
        if (_grade.value == grade) {
            _grade.value = null
        } else {
            _grade.value = grade
        }
        updateCategoryFilters()
    }

    fun updateCourse(course: Course) {
        if (_course.value == course) {
            _course.value = null
        } else {
            _course.value = course
        }
        updateCategoryFilters()
    }

    fun updateCategory(category: RepositoryCategory) {
        if (_category.value != category) {
            _category.value = category
            loadCategory(category)
        }
    }

    private fun courseFilter(item: RepositoryItem, currentCourse: Course?, year: Int?): Boolean {
        if (currentCourse == null || year == null) {
            return true
        }

        val part = item.name.split('-').getOrNull(1) ?: return true
        val groupYear = part.toIntOrNull() ?: return true

        if ((year % 100 - (currentCourse.number - 1)) == groupYear) {
            return true
        }

        return false
    }

    private fun updateCategoryFilters() {
        val cache = _repositoryItemsCache ?: return

        val currentGrade = _grade.value
        val currentCourse = _course.value
        val currentYear = _category.value?.year

        viewModelScope.launch {
            val filterItems = cache
                .filter { item ->
                    gradeFilter(item, currentGrade)
                }
                .filter { item ->
                    courseFilter(item, currentCourse, currentYear)
                }

            _repositoryItems.value = State.success(filterItems)
        }
    }
}