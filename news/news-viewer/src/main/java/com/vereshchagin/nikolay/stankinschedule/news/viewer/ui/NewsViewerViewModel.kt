package com.vereshchagin.nikolay.stankinschedule.news.viewer.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vereshchagin.nikolay.stankinschedule.core.ui.UIState
import com.vereshchagin.nikolay.stankinschedule.news.core.domain.model.NewsContent
import com.vereshchagin.nikolay.stankinschedule.news.viewer.domain.usecase.NewsViewerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class NewsViewerViewModel @Inject constructor(
    private val viewerUseCase: NewsViewerUseCase,
) : ViewModel() {

    private val _newsContent = MutableStateFlow<UIState<NewsContent>>(UIState.loading())
    val newsContent = _newsContent.asStateFlow()

    fun loadNewsContent(postId: Int, force: Boolean = false) {
        _newsContent.value = UIState.loading()

        viewModelScope.launch {
            viewerUseCase.loadNewsContent(postId, force)
                .catch { e ->
                    _newsContent.value = UIState.failed(e)
                }
                .collect { data ->
                    _newsContent.value = data
                }
        }
    }
}