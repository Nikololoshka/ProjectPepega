package com.vereshchagin.nikolay.stankinschedule.schedule.viewer.data.mapper

import android.util.Patterns
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.PairModel
import com.vereshchagin.nikolay.stankinschedule.schedule.viewer.domain.model.LinkContent
import com.vereshchagin.nikolay.stankinschedule.schedule.viewer.domain.model.ScheduleViewPair
import com.vereshchagin.nikolay.stankinschedule.schedule.viewer.domain.model.TextContent
import com.vereshchagin.nikolay.stankinschedule.schedule.viewer.domain.model.ViewContent

fun PairModel.toViewPair(): ScheduleViewPair {
    return ScheduleViewPair(
        id = info.id,
        title = title,
        lecturer = lecturer,
        classroom = classroomViewContent(classroom),
        subgroup = subgroup,
        type = type,
        startTime = time.startString(),
        endTime = time.endString()
    )
}

private fun classroomViewContent(classroom: String): ViewContent {

    var name = classroom
    val links = mutableListOf<LinkContent.Link>()

    val match = Patterns.WEB_URL.matcher(classroom)
    while (match.find()) {

        val url = match.group()
        val urlName = match.group(3) ?: "url name"
        val start = name.indexOf(url)

        name = name.replace(url, urlName)

        links.add(
            LinkContent.Link(start, urlName.length, url)
        )
    }

    if (links.isEmpty()) {
        return TextContent(classroom)
    }

    return LinkContent(
        name = name,
        links = links
    )
}
