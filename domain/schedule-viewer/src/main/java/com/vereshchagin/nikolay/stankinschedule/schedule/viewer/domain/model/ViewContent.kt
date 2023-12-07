package com.vereshchagin.nikolay.stankinschedule.schedule.viewer.domain.model

interface ViewContent {

    fun isEmpty(): Boolean

}

fun ViewContent.isNotEmpty() = !this.isEmpty()