package com.vereshchagin.nikolay.stankinschedule.core.ui.components

import androidx.compose.runtime.Composable
import com.vereshchagin.nikolay.stankinschedule.core.ui.UIState

@Composable
inline fun <T : Any> Stateful(
    state: UIState<T>,
    onSuccess: @Composable (data: T) -> Unit,
    onLoading: @Composable () -> Unit,
    onFailed: @Composable (error: Throwable) -> Unit,
) {
    when (state) {
        is UIState.Success -> onSuccess(state.data)
        is UIState.Failed -> onFailed(state.error)
        is UIState.Loading -> onLoading()
    }
}

@Composable
inline fun <T : Any?> Stateful(
    state: UIState<T>,
    onSuccess: @Composable (data: T) -> Unit,
    onLoading: @Composable () -> Unit,
) {
    when (state) {
        is UIState.Success -> onSuccess(state.data)
        else -> onLoading()
    }
}