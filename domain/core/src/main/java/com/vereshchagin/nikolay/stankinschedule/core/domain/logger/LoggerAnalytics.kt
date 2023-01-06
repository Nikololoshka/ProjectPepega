package com.vereshchagin.nikolay.stankinschedule.core.domain.logger

interface LoggerAnalytics {

    fun logEvent(type: String, value: String)

    fun recordException(t: Throwable)

    companion object {
        const val SCREEN_ENTER = "screen_enter_view"
        const val SCREEN_LEAVE = "screen_leave_view"
    }
}