package com.vereshchagin.nikolay.stankinschedule.utils

import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewStubProxy
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.vereshchagin.nikolay.stankinschedule.utils.extensions.inflateView


class StatefulLayout2(
    private val root: ViewGroup, initKey: Int, initView: View,
) : LifecycleObserver {

    // TODO(03/03/2021 возможно необходимо синхронизировать состояния)
    private val states = HashMap<Int, View>()
    private val stubs = HashMap<Int, ViewStubProxy>()
    private var currentState: Int

    init {
        states[initKey] = initView
        currentState = initKey
    }

    fun addView(key: Int, view: View) {
        states[key] = view
        view.visibility = View.GONE
    }

    fun addView(key: Int, view: ViewStubProxy) {
        stubs[key] = view
    }

    fun setState(key: Int) {
        if (key == currentState) {
            return
        }

        var oldState = states[currentState]
        if (oldState == null) {
            oldState = stubs[currentState]!!.inflateView()
        }

        var newState = states[key]
        if (newState == null) {
            newState = stubs[key]!!.inflateView()
        }

        AnimationUtils.fade(oldState, false)
        AnimationUtils.fade(newState, true)

        currentState = key
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun clear() {
        states.clear()
        stubs.clear()
    }

    class Builder(private val root: ViewGroup) {

        private var lifecycleOwner: LifecycleOwner? = null

        private var initKey: Int? = null
        private var initView: View? = null

        private val states = HashMap<Int, View>()
        private val stubs = HashMap<Int, ViewStubProxy>()

        fun init(key: Int, view: View): Builder {
            initKey = key
            initView = view
            return this
        }

        fun addView(key: Int, view: View): Builder {
            states[key] = view
            return this
        }

        fun addView(key: Int, view: ViewStubProxy): Builder {
            stubs[key] = view
            return this
        }

        fun setOwner(owner: LifecycleOwner): Builder {
            lifecycleOwner = owner
            return this
        }

        fun create(): StatefulLayout2 {
            val stateful = StatefulLayout2(root, initKey!!, initView!!)
            for ((key, view) in states) {
                stateful.addView(key, view)
            }
            for ((key, view) in stubs) {
                stateful.addView(key, view)
            }

            lifecycleOwner?.lifecycle?.addObserver(stateful)
            return stateful
        }
    }

    companion object {
        const val LOADING = -1
        const val ERROR = -2
        const val CONTENT = -3
        const val EMPTY = -4
    }
}