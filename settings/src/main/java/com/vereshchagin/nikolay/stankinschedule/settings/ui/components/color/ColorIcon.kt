package com.vereshchagin.nikolay.stankinschedule.settings.ui.components.color

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun ColorIcon(
    color: Color,
    modifier: Modifier = Modifier.size(24.dp),
    colorBorder: Color = MaterialTheme.colorScheme.onSurface
) {
    Canvas(
        modifier = modifier
    ) {
        drawRect(color = color, size = this.size)
        drawRect(color = colorBorder, size = this.size, style = Stroke(width = 2f))
    }
}