package com.vereshchagin.nikolay.stankinschedule.modulejournal.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.vereshchagin.nikolay.stankinschedule.core.ui.theme.Dimen
import com.vereshchagin.nikolay.stankinschedule.modulejournal.domain.model.Student

/*
@Preview(showSystemUi = true, showBackground = true)
@Composable
fun StudentInfoPreview() {
    StudentInfo(
        student = Student(
            name = "Nikolay",
            group = "ИДБ-17-09",
            semesters = listOf(
                "2022-Весна", "2021-Осень"
            )
        ),
        modifier = Modifier.fillMaxSize()
    )
}
*/

object StudentInfoDefault {
    val TabsHeight = 48.dp
}

@Composable
fun StudentInfo(
    student: Student,
    collapseValue: Float,
    selectedSemester: Int,
    onSemesterSelect: (index: Int) -> Unit,
    modifier: Modifier = Modifier,
) {

    val currentCollapsing by animateFloatAsState(targetValue = collapseValue)

    Box(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .alpha(currentCollapsing)
        ) {
            Text(
                text = student.name,
                style = MaterialTheme.typography.h5,
                modifier = Modifier
                    .padding(
                        top = Dimen.ContentPadding,
                        start = Dimen.ContentPadding
                    )
                    .fillMaxWidth()
            )
            Text(
                text = student.group,
                style = MaterialTheme.typography.body1,
                modifier = Modifier
                    .padding(
                        start = Dimen.ContentPadding
                    )
                    .fillMaxWidth()
            )
            Text(
                text = "Predict rating: --.--",
                textAlign = TextAlign.End,
                modifier = Modifier
                    .padding(end = Dimen.ContentPadding)
                    .fillMaxWidth()
            )
            Text(
                text = "Rating: --.--",
                textAlign = TextAlign.End,
                modifier = Modifier
                    .padding(end = Dimen.ContentPadding)
                    .fillMaxWidth()
            )
        }
        ScrollableTabRow(
            selectedTabIndex = selectedSemester,
            edgePadding = 0.dp,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomStart)
                .requiredHeight(StudentInfoDefault.TabsHeight)
        ) {
            student.semesters.forEachIndexed { index, semester ->
                Tab(
                    selected = index == selectedSemester,
                    onClick = { onSemesterSelect(index) },
                    text = {
                        Text(text = semester)
                    }
                )
            }
        }
    }
}