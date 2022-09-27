package com.vereshchagin.nikolay.stankinschedule.schedule.core.ui.data

import android.net.Uri
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.PairModel

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
    val uri = Uri.parse(classroom)
    val host = uri.host ?: return ViewContent.TextContent(classroom)

    return ViewContent.LinkContent(
        name = host
            .removePrefix(prefix = "www.")
            .substringBeforeLast(delimiter = '.'),
        link = classroom
    )
}