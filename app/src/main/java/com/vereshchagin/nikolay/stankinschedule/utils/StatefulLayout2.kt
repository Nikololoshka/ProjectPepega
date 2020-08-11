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

    companion object {
        const val LOADING = -1
        const val ERROR = -2
        const val CONTENT = -3
        const val EMPTY = -4
    }
}