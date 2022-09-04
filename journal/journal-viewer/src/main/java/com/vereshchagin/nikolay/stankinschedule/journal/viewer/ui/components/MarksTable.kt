package com.vereshchagin.nikolay.stankinschedule.journal.viewer.ui.components

import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.vereshchagin.nikolay.stankinschedule.journal.core.domain.model.SemesterMarks
import com.vereshchagin.nikolay.stankinschedule.journal.viewer.ui.view.MarksTableView

@Composable
fun MarksTable(
    semesterMarks: SemesterMarks,
    modifier: Modifier = Modifier,
) {
    AndroidView(
        factory = { context ->
            MarksTableView(
                context = context
            ).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }
        },
        update = {
            it.setSemesterMarks(semesterMarks)
        },
        modifier = modifier
    )
}
