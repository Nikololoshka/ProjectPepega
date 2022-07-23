package com.vereshchagin.nikolay.stankinschedule.news.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vereshchagin.nikolay.stankinschedule.core.ui.State
import com.vereshchagin.nikolay.stankinschedule.news.domain.model.NewsContent
import com.vereshchagin.nikolay.stankinschedule.news.domain.usecase.NewsViewerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class NewsViewerViewModel @Inject constructor(
    private val viewerUseCase: NewsViewerUseCase,
) : ViewModel() {

    private val _newsContent = MutableStateFlow<State<NewsContent>>(State.loading())
    val newsContent = _newsContent.asStateFlow()

    fun loadNewsContent(postId: Int, force: Boolean = false) {
        _newsContent.value = State.loading()

        viewModelScope.launch {
            viewerUseCase.loadNewsContent(postId, force)
                .catch { e ->
                    _newsContent.value = State.failed(e)
                }
                .collect { data ->
                    delay(timeMillis = 500) // TODO("Задержка на отображение загрузки")
                    _newsContent.value = data
                }
        }
    }
}