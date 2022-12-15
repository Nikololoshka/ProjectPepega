package com.vereshchagin.nikolay.stankinschedule.schedule.viewer.domain.model

class TextContent(val content: String) : ViewContent {
    override fun isEmpty(): Boolean = content.isEmpty()
}