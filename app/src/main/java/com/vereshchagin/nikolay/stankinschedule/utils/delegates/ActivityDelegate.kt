package com.vereshchagin.nikolay.stankinschedule.utils.delegates

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Базовый делегат, который устанавливает и очищает слушателя активности
 * при создании и уничтожении компонента.
 */
class ActivityDelegate<T> : ReadWriteProperty<AppCompatActivity, T>, LifecycleObserver {

    private var value: T? = null

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun onDestroy() {
        value = null
    }

    override operator fun setValue(thisRef: AppCompatActivity, property: KProperty<*>, value: T) {
        thisRef.lifecycle.removeObserver(this)
        this.value = value
        thisRef.lifecycle.addObserver(this)
    }

    override operator fun getValue(thisRef: AppCompatActivity, property: KProperty<*>): T {
        return this.value!!
    }
}