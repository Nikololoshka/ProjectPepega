package com.vereshchagin.nikolay.stankinschedule.ui

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding

/**
 * Базовая активность.
 */
abstract class BaseActivity<T : ViewBinding>(
    private val inflateMethod: (LayoutInflater) -> T,
) : AppCompatActivity(), BaseComponent {

    protected lateinit var binding: T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = onInflateView()
        setContentView(binding.root)
        onPostCreateView(savedInstanceState)
    }

    /**
     * Создает объект ViewBinding.
     */
    protected open fun onInflateView(): T {
        return inflateMethod(layoutInflater)
    }

    /**
     * Вызывается после создания View для дальнейшей инициализации.
     */
    abstract fun onPostCreateView(savedInstanceState: Bundle?)
}