package com.vereshchagin.nikolay.stankinschedule.core.domain.repository

import android.util.Log

class ConsoleLoggerAnalytics : LoggerAnalytics {

    override fun logEvent(type: String, value: String) {
        Log.i("LoggerAnalytics", "$type: $value")
    }

    override fun recordException(t: Throwable) {
        Log.e("LoggerAnalytics", t.toString(), t)
    }
}