package com.vereshchagin.nikolay.stankinschedule.core.ui.components

import androidx.compose.runtime.Composable
import com.vereshchagin.nikolay.stankinschedule.core.ui.State

@Composable
inline fun <T : Any> Stateful(
    state: State<T>,
    onSuccess: @Composable (data: T) -> Unit,
    onLoading: @Composable () -> Unit,
    onFailed: @Composable (error: Throwable) -> Unit,
) {
    when (state) {
        is State.Success -> onSuccess(state.data)
        is State.Failed -> onFailed(state.error)
        is State.Loading -> onLoading()
    }
}