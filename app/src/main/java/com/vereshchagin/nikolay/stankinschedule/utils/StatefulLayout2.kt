package com.vereshchagin.nikolay.stankinschedule.utils

import android.view.View
import android.view.ViewGroup


class StatefulLayout2(
    private val root: ViewGroup, initKey: Int, initView: View
) {

    private val states = HashMap<Int, View>()
    private var currentState: Int

    init {
        states[initKey] = initView
        currentState = initKey
    }

    fun addView(key: Int, view: View) {
        states[key] = view
        view.visibility = View.GONE
    }

    fun setState(key: Int) {
        if (key == currentState) {
            return
        }

        AnimationUtils.fade(states[currentState]!!, false)
        AnimationUtils.fade(states[key]!!, true)

        currentState = key
    }

    class Builder(private val root: ViewGroup) {

        private var initKey: Int? = null
        private var initView: View? = null

        private val states = HashMap<Int, View>()

        fun init(key: Int, view: View): Builder {
            initKey = key
            initView = view
            return this
        }

        fun addView(key: Int, view: View): Builder {
            states[key] = view
            return this
        }

        fun create(): StatefulLayout2 {
            val stateful = StatefulLayout2(root, initKey!!, initView!!)
            for ((key, view) in states) {
                stateful.addView(key, view)
            }
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