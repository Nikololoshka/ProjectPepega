package com.vereshchagin.nikolay.stankinschedule.schedule.viewer.domain.model

class LinkContent(
    val name: String,
    val links: List<Link>
) : ViewContent {

    override fun isEmpty(): Boolean = links.isEmpty()

    class Link(
        val position: Int,
        val lenght: Int,
        val url: String
    )
}