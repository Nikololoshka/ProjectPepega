package com.vereshchagin.nikolay.stankinschedule.modulejournal.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vereshchagin.nikolay.stankinschedule.modulejournal.domain.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JournalLoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
) : ViewModel() {

    private val _loginState = MutableStateFlow(false)
    val loginState = _loginState.asStateFlow()

    private val _isLogging = MutableStateFlow(false)
    val isLogging = _isLogging.asStateFlow()

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError = _loginError.asStateFlow()


    fun login(login: String, password: String) {
        viewModelScope.launch {
            _loginError.value = null
            _isLogging.value = true

            loginUseCase.login(login, password)
                .catch { e ->
                    _loginError.value = e.toString()
                }
                .collect {
                    _loginState.value = true
                }

            _isLogging.value = false
        }
    }
}