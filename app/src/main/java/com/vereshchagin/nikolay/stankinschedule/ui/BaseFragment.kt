package com.vereshchagin.nikolay.stankinschedule.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.viewbinding.ViewBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import com.vereshchagin.nikolay.stankinschedule.R

/**
 * Базовый фрагмент реализацией с ViewBinding.
 */
abstract class BaseFragment<T : ViewBinding> : Fragment() {

    protected var _binding: T? = null
    protected val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = onInflateView(inflater, container, savedInstanceState)
        onPostCreateView(savedInstanceState)
        return binding.root
    }

    /**
     * Создает объект ViewBinding.
     */
    abstract fun onInflateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): T

    /**
     * Вызывается после создания View для дальнейшей инициализации.
     */
    abstract fun onPostCreateView(savedInstanceState: Bundle?)

    /**
     * Осуществляет переход к новому фрагменту / активности с заданными аргументами.
     */
    protected fun navigateTo(
        @IdRes destination: Int,
        args: Bundle? = null,
        options: NavOptions? = null,
    ) {
        val navController = Navigation.findNavController(requireActivity(), R.id.nav_host)
        navController.navigate(destination, args, options)
    }

    /**
     * Показывает SnackBar для корневого View.
     */
    protected fun showSnack(
        @StringRes id: Int,
        duration: Int = Snackbar.LENGTH_SHORT,
        args: Array<String> = arrayOf(),
    ) {
        Snackbar.make(binding.root, getString(id, *args), duration)
            .show()
    }

    /**
     * Показывает SnackBar для корневого View.
     */
    protected fun showSnack(message: String, duration: Int = Snackbar.LENGTH_SHORT) {
        Snackbar.make(binding.root, message, duration)
            .show()
    }

    /**
     * Добавление информации в FirebaseAnalytics о включенном фрагменте.
     */
    protected fun trackScreen(screenName: String) {
        Firebase.analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
            param(FirebaseAnalytics.Param.SCREEN_CLASS, screenName)
        }
    }

    /**
     * Скрывает клавиатуру с экрана.
     */
    protected fun hideKeyboard() {
        val activity = requireActivity()
        val manager = ContextCompat.getSystemService(activity, InputMethodManager::class.java)
        val currentFocusedView = activity.currentFocus
        if (currentFocusedView != null && manager != null) {
            manager.hideSoftInputFromWindow(
                currentFocusedView.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}