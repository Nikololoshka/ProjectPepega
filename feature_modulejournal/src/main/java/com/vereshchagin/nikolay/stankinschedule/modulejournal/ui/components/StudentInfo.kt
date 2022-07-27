package com.vereshchagin.nikolay.stankinschedule.modulejournal.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.vereshchagin.nikolay.stankinschedule.core.ui.theme.Dimen
import com.vereshchagin.nikolay.stankinschedule.modulejournal.R
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


@Composable
fun StudentInfo(
    student: Student,
    rating: String?,
    predictRating: String?,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
    ) {
        Image(
            painter = painterResource(R.drawable.background_mj),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            colorFilter = ColorFilter.tint(
                color = if (isSystemInDarkTheme()) {
                    MaterialTheme.colors.background.copy(alpha = 0.75f)
                } else {
                    MaterialTheme.colors.primary.copy(alpha = 0.5f)
                },
                blendMode = BlendMode.Multiply
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = Dimen.ContentPadding,
                    start = Dimen.ContentPadding,
                    end = Dimen.ContentPadding
                )
        ) {
            Text(
                text = student.name,
                color = Color.White,
                style = MaterialTheme.typography.h5,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = student.group,
                color = Color.White,
                style = MaterialTheme.typography.body1,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = stringResource(R.string.predict_rating, predictRating ?: "--"),
                color = Color.White,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = stringResource(R.string.current_rating, rating ?: "--"),
                color = Color.White,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}