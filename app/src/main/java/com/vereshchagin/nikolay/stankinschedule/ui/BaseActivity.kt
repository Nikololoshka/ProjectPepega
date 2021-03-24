package com.vereshchagin.nikolay.stankinschedule.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding

abstract class BaseActivity<T : ViewBinding> : AppCompatActivity(), BaseComponent {

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
    abstract fun onInflateView(): T

    /**
     * Вызывается после создания View для дальнейшей инициализации.
     */
    abstract fun onPostCreateView(savedInstanceState: Bundle?)
}