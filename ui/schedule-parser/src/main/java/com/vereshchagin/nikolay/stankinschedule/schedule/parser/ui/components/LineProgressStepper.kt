package com.vereshchagin.nikolay.stankinschedule.schedule.parser.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.vereshchagin.nikolay.stankinschedule.core.ui.theme.AppTheme


@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun LineProgressStepperPreview() {
    AppTheme {
        Surface {
            LineProgressStepper(
                step = 1,
                count = 4,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(8.dp)
            )
        }
    }
}

@Composable
fun LineProgressStepper(
    step: Int,
    count: Int,
    modifier: Modifier = Modifier,
    progressColor: Color = MaterialTheme.colorScheme.primary,
    stepColor: Color = MaterialTheme.colorScheme.primaryContainer,
    stroke: Dp = 4.dp,
    space: Dp = 4.dp
) {
    Canvas(
        modifier = modifier
    ) {
        val stepSpace = space.toPx()
        val stepLength = size.width / count - stepSpace
        val y = size.height / 2

        for (index in 0 until count) {
            drawLine(
                color = if (index <= step - 1) progressColor else stepColor,
                start = Offset(x = (stepLength + stepSpace) * index, y = y),
                end = Offset(x = (stepLength + stepSpace) * index + stepLength, y = y),
                strokeWidth = stroke.toPx()
            )
        }
    }
}