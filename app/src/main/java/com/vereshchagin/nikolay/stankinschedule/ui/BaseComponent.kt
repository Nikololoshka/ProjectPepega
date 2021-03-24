package com.vereshchagin.nikolay.stankinschedule.ui

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase

/**
 * Интерфейс с функциями для UI компоненты (Активности / Фрагмента).
 */
interface BaseComponent {
    /**
     * Добавление информации в FirebaseAnalytics о включенном фрагменте.
     */
    fun trackScreen(screenName: String, screenClass: String = screenName) {
        Firebase.analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
            param(FirebaseAnalytics.Param.SCREEN_CLASS, screenClass)
        }
    }
}