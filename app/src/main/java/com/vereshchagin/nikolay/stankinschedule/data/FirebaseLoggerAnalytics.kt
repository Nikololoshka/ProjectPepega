package com.vereshchagin.nikolay.stankinschedule.data

import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.vereshchagin.nikolay.stankinschedule.core.domain.repository.LoggerAnalytics
import javax.inject.Inject

class FirebaseLoggerAnalytics @Inject constructor() : LoggerAnalytics {

    private val analytics = Firebase.analytics
    private val crashlytics = Firebase.crashlytics

    override fun logEvent(type: String, value: String) {
        analytics.logEvent(type) { param("event", value) }
    }

    override fun recordException(t: Throwable) {
        crashlytics.recordException(t)
    }
}