package com.vereshchagin.nikolay.stankinschedule.modulejournal.ui.components

import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.vereshchagin.nikolay.stankinschedule.modulejournal.domain.model.SemesterMarks
import com.vereshchagin.nikolay.stankinschedule.modulejournal.ui.view.MarksTable as MarksTableView

@Composable
fun MarksTable(
    semesterMarks: SemesterMarks,
    modifier: Modifier = Modifier,
) {
    AndroidView(
        factory = { context ->
            MarksTableView(context).apply {
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
