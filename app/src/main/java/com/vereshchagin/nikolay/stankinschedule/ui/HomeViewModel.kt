package com.vereshchagin.nikolay.stankinschedule.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vereshchagin.nikolay.stankinschedule.core.ui.UIState
import com.vereshchagin.nikolay.stankinschedule.news.core.domain.model.NewsPost
import com.vereshchagin.nikolay.stankinschedule.news.home.domain.usecase.NewsHomeUseCase
import com.vereshchagin.nikolay.stankinschedule.schedule.home.domain.model.ScheduleHomeInfo
import com.vereshchagin.nikolay.stankinschedule.schedule.home.domain.usecase.ScheduleHomeUseCase
import com.vereshchagin.nikolay.stankinschedule.schedule.settings.domain.model.PairColorGroup
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
    private val scheduleUseCase: ScheduleHomeUseCase,
    private val newsUseCase: NewsHomeUseCase
) : ViewModel() {

    val pairColorGroup: Flow<PairColorGroup> = scheduleUseCase.pairColorGroup()

    private val _news = MutableStateFlow<List<NewsPost>>(emptyList())
    val news = _news.asStateFlow()

    private val _days = MutableStateFlow<UIState<ScheduleHomeInfo?>>(UIState.loading())
    val days = _days.asStateFlow()

    init {
        val delta = 3
        viewModelScope.launch {
            scheduleUseCase.favoriteSchedule(
                LocalDate.now().minusDays(delta),
                LocalDate.now().plusDays(delta + 1)
            ).collectLatest {
                _days.value = UIState.success(it)
            }
        }

        viewModelScope.launch {
            newsUseCase.refreshAll(force = false)
            newsUseCase.news(newsCount = NEWS_COUNT).collectLatest {
                _news.value = it
            }
        }
    }

    companion object {
        const val NEWS_COUNT = 20
    }
}