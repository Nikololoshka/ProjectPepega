package com.vereshchagin.nikolay.stankinschedule.schedule.parser.ui.components

import android.content.res.Configuration
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.center
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toOffset
import com.vereshchagin.nikolay.stankinschedule.core.ui.theme.AppTheme
import com.vereshchagin.nikolay.stankinschedule.core.ui.theme.Dimen

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ProgressStepperPreview() {
    AppTheme {
        Scaffold(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) { innerPadding ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Dimen.ContentPadding),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(4.dp)
            ) {
                ProgressStepper(
                    step = 1,
                    count = 4,
                    modifier = Modifier
                        .fillMaxHeight()
                        .aspectRatio(1f)
                )

                Column(
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Current step",
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = "Next step detail",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalTextApi::class)
@Composable
fun ProgressStepper(
    step: Int,
    count: Int,
    modifier: Modifier = Modifier,
    progressColor: Color = MaterialTheme.colorScheme.primary,
    backgroundColor: Color = MaterialTheme.colorScheme.primaryContainer,
    stroke: Dp = 3.dp,
    stepFormatter: (step: Int, count: Int) -> String = { s, c -> "$s/$c" },
    stepColor: Color = MaterialTheme.colorScheme.onBackground
) {

    val progress by animateFloatAsState(targetValue = step * 360f / count)

    val textMeasurer = rememberTextMeasurer()
    val textLayoutResult = remember(step, count) {
        textMeasurer.measure(text = stepFormatter(step, count))
    }

    Canvas(modifier = modifier) {
        val strokeWidth = stroke.toPx()

        // background
        drawCircle(
            color = backgroundColor,
            style = Stroke(width = strokeWidth)
        )

        // progress
        drawArc(
            color = progressColor,
            startAngle = -90f,
            sweepAngle = progress,
            useCenter = false,
            style = Stroke(width = strokeWidth)
        )

        drawText(
            textLayoutResult = textLayoutResult,
            color = stepColor,
            topLeft = size.center - textLayoutResult.size.center.toOffset()
        )
    }
}