package com.vereshchagin.nikolay.stankinschedule.schedule.viewer.domain.model

interface ViewContent {

    fun isEmpty(): Boolean

    class TextContent(val content: String) : ViewContent {
        override fun isEmpty(): Boolean = content.isEmpty()

    }

    class LinkContent(val name: String, val link: String) : ViewContent {
        override fun isEmpty(): Boolean = link.isEmpty()
    }
}