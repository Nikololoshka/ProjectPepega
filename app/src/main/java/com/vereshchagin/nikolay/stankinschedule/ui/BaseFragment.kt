package com.vereshchagin.nikolay.stankinschedule.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.viewbinding.ViewBinding
import com.google.android.material.snackbar.Snackbar
import com.vereshchagin.nikolay.stankinschedule.R

/**
 * Базовый фрагмент с ViewBinding.
 */
abstract class BaseFragment<T : ViewBinding> : Fragment() {

    private var _binding: T? = null
    protected val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
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
        savedInstanceState: Bundle?
    ) : T

    /**
     * Вызывается после создания View для дальнейшей инициализации.
     */
    abstract fun onPostCreateView(savedInstanceState: Bundle?)

    /**
     * Осуществляет переход к новому элементу.
     * @param destination ID элемента назначения.
     * @param args аргументы для перехода.
     */
    protected fun navigateTo(@IdRes destination: Int, args: Bundle? = null)  {
        val navController = Navigation.findNavController(requireActivity(), R.id.nav_host)
        navController.navigate(destination, args)
    }

    /**
     * Показывает SnackBar для корневого View.
     * @param id ID сообщения.
     * @param duration продолжительность показа.
     */
    protected fun showSnack(@StringRes id: Int, duration: Int = Snackbar.LENGTH_SHORT, args: Array<String> = arrayOf()) {
        Snackbar.make(binding.root, getString(id, *args), duration)
            .show()
    }

    /**
     * Показывает SnackBar для корневого View.
     * @param message сообщение.
     * @param duration продолжительность показа.
     */
    protected fun showSnack(message: String, duration: Int = Snackbar.LENGTH_SHORT) {
        Snackbar.make(binding.root, message, duration)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}