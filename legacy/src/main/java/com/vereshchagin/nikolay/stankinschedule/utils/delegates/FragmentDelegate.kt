package com.vereshchagin.nikolay.stankinschedule.utils.delegates

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Базовый делегат, который устанавливает и очищает слушателя фрагмента
 * при создании и уничтожении компонента.
 */
class FragmentDelegate<T> : ReadWriteProperty<Fragment, T>, LifecycleObserver {

    private var value: T? = null

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun onDestroy() {
        value = null
    }

    override operator fun setValue(thisRef: Fragment, property: KProperty<*>, value: T) {
        thisRef.viewLifecycleOwner.lifecycle.removeObserver(this)
        this.value = value
        thisRef.viewLifecycleOwner.lifecycle.addObserver(this)
    }

    override operator fun getValue(thisRef: Fragment, property: KProperty<*>): T {
        return this.value!!
    }
}