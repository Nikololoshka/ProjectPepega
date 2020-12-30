package com.vereshchagin.nikolay.stankinschedule.ui.modulejournal.login

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.vereshchagin.nikolay.stankinschedule.repository.ModuleJournalRepository
import com.vereshchagin.nikolay.stankinschedule.utils.State
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * ViewModel фрагмента входа в модульный журнал.
 */
class ModuleJournalLoginViewModel(
    private val repository: ModuleJournalRepository
) : ViewModel() {

    /**
     * Статус авторизации в модульном журнале.
     *
     * Status.success(true) - вход выполнен успешно.
     * Status.success(false) - ожидание выполнения входа.
     */
    val authorizedState = MutableLiveData<State<Boolean>>(State.success(false))

    /**
     * Выполняет авторизацию в модульном журнале.
     */
    fun signIn(userLogin: String, userPassword: String) {
        viewModelScope.launch {
            repository.signIn(userLogin, userPassword)
                .catch { e ->
                    authorizedState.value = State.failed(e)
                }.collect { state ->
                    authorizedState.value = state
                }
        }
    }

    /**
     * Фабрика для создания ViewModel.
     */
    class Factory(
        private val application: Application
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return ModuleJournalLoginViewModel(
                ModuleJournalRepository(application.cacheDir)
            ) as T
        }
    }
}