package com.vereshchagin.nikolay.stankinschedule.utils.delegates

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Base delegate that sets and disposes the fragment's listener when the fragment is
 * created and destroyed.
 */
class FragmentDelegate<T>(
    fragment: Fragment,
) : ReadWriteProperty<Fragment, T>, LifecycleObserver {

    private var value: T? = null

    init {
        fragment.lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun destroy() {
        value = null
    }

    override operator fun setValue(thisRef: Fragment, property: KProperty<*>, value: T) {
        this.value = value
    }

    override operator fun getValue(thisRef: Fragment, property: KProperty<*>): T {
        return this.value!!
    }
}