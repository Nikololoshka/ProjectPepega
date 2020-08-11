package com.vereshchagin.nikolay.stankinschedule.api

enum class Status {
    RUNNING,
    SUCCESS,
    FAILED
}

data class NetworkState(val status: Status, val msg: String? = null) {

    companion object {
        val LOADED = NetworkState(
            Status.SUCCESS
        )
        val LOADING = NetworkState(
            Status.RUNNING
        )

        fun error(msg: String?) =
            NetworkState(
                Status.FAILED,
                msg
            )
    }
}