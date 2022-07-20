package com.vereshchagin.nikolay.stankinschedule.ui.modulejournal.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vereshchagin.nikolay.stankinschedule.repository.ModuleJournalRepository
import com.vereshchagin.nikolay.stankinschedule.utils.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel фрагмента входа в модульный журнал.
 */
@HiltViewModel
class ModuleJournalLoginViewModel @Inject constructor(
    private val repository: ModuleJournalRepository,
) : ViewModel() {


    private val _authorizedState = MutableStateFlow<State<Boolean>>(State.success(false))

    /**
     * Статус авторизации в модульном журнале.
     *
     * Status.success(true) - вход выполнен успешно.
     * Status.success(false) - ожидание выполнения входа.
     */
    val authorizedState = _authorizedState.asStateFlow()

    /**
     * Выполняет авторизацию в модульном журнале.
     */
    fun signIn(userLogin: String, userPassword: String) {
        viewModelScope.launch {
            repository.signIn(userLogin, userPassword)
                .catch { e ->
                    _authorizedState.value = State.failed(e)
                }.collect { state ->
                    _authorizedState.value = state
                }
        }
    }
}