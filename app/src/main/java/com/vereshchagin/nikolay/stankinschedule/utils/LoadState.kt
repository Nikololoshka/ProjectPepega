package com.vereshchagin.nikolay.stankinschedule.utils

class LoadState private constructor(val state: State, val msg: String = "") {

    enum class State {
        RUNNING,
        SUCCESS,
        FAILED
    }

    companion object {
        val LOADING = LoadState(State.RUNNING)
        val LOADED = LoadState(State.SUCCESS)

        fun error(msg: String) = LoadState(State.FAILED, msg)
    }
}