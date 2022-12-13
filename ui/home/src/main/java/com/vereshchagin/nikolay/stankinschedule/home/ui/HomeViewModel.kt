package com.vereshchagin.nikolay.stankinschedule.home.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vereshchagin.nikolay.stankinschedule.core.ui.components.UIState
import com.vereshchagin.nikolay.stankinschedule.news.core.domain.model.NewsPost
import com.vereshchagin.nikolay.stankinschedule.news.core.domain.usecase.NewsReviewUseCase
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.ScheduleInfo
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.ScheduleModel
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.usecase.ScheduleUseCase
import com.vereshchagin.nikolay.stankinschedule.schedule.settings.domain.model.PairColorGroup
import com.vereshchagin.nikolay.stankinschedule.schedule.settings.domain.usecase.ScheduleSettingsUseCase
import com.vereshchagin.nikolay.stankinschedule.schedule.viewer.domain.model.ScheduleViewDay
import com.vereshchagin.nikolay.stankinschedule.schedule.viewer.domain.usecase.ScheduleViewerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.joda.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val scheduleUseCase: ScheduleUseCase,
    private val scheduleViewerUseCase: ScheduleViewerUseCase,
    private val scheduleSettingsUseCase: ScheduleSettingsUseCase,
    private val newsUseCase: NewsReviewUseCase,
) : ViewModel() {

    val pairColorGroup: Flow<PairColorGroup> = scheduleSettingsUseCase.pairColorGroup()

    private val _favorite = MutableStateFlow<ScheduleInfo?>(null)
    val favorite = _favorite.asStateFlow()

    private val _days = MutableStateFlow<UIState<List<ScheduleViewDay>>>(UIState.loading())
    val days = _days.asStateFlow()

    private val _news = MutableStateFlow<List<NewsPost>>(emptyList())
    val news = _news.asStateFlow()

    init {
        val delta = 3
        viewModelScope.launch {
            scheduleSettingsUseCase.favorite().collectLatest { id ->
                scheduleUseCase.scheduleModel(id).collectLatest { model ->
                    updateScheduleBlock(model, delta)
                }
            }
        }

        viewModelScope.launch {
            newsUseCase.lastNews(newsCount = NEWS_COUNT).collectLatest {
                _news.value = it
            }
        }

        viewModelScope.launch {
            newsUseCase.refreshAllNews(force = false)
        }
    }

    private fun updateScheduleBlock(model: ScheduleModel?, delta: Int) {
        if (model == null) {
            _favorite.value = null
            _days.value = UIState.Success(emptyList())
            return
        }

        val days = scheduleViewerUseCase.scheduleViewDays(
            model = model,
            from = LocalDate.now().minusDays(delta),
            to = LocalDate.now().plusDays(delta + 1)
        )

        _favorite.value = model.info
        _days.value = UIState.success(days)
    }

    companion object {
        const val NEWS_COUNT = 20
    }
}