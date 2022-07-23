package com.vereshchagin.nikolay.stankinschedule.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun AppTabIndicator(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colors.primary,
) {
    Spacer(
        modifier
            .padding(horizontal = 24.dp)
            .height(4.dp)
            .background(
                color = color,
                shape = RoundedCornerShape(
                    topStartPercent = 100,
                    topEndPercent = 100
                )
            )
    )
}