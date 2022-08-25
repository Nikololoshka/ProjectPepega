package com.vereshchagin.nikolay.stankinschedule.schedule.editor.ui

import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.PairModel

sealed interface PairEditorState {
    object Loading: PairEditorState
    class Content(val pair: PairModel?) : PairEditorState
    class Error(val t: Throwable): PairEditorState
    object Complete : PairEditorState
}