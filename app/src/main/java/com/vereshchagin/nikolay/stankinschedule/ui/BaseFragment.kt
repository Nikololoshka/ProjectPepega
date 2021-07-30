package com.vereshchagin.nikolay.stankinschedule.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.viewbinding.ViewBinding
import com.google.android.material.snackbar.Snackbar
import com.vereshchagin.nikolay.stankinschedule.R

/**
 * Базовый фрагмент.
 */
abstract class BaseFragment<T : ViewBinding>(
    private val inflateMethod: (LayoutInflater, ViewGroup?, Boolean) -> T
) : Fragment(), BaseComponent {

    private var _binding: T? = null
    protected val binding get() = _binding!!
    protected val rawBinding = _binding

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
    protected open fun onInflateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): T {
        return inflateMethod(inflater, container, false)
    }

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
     * Устанавливает название в action bar.
     */
    protected fun setActionBarTitle(title: String?) {
        (requireActivity() as AppCompatActivity).supportActionBar?.title = title
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