package com.vereshchagin.nikolay.stankinschedule.core.ui.components

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.vereshchagin.nikolay.stankinschedule.core.ui.theme.Dimen

@Composable
fun RadioGroup(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column {
        Text(
            text = title,
            modifier = Modifier
                .padding(bottom = Dimen.ContentPadding)
        )
        content()
    }
}

@Composable
fun RadioItem(
    title: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                onClick = onClick,
                interactionSource = interactionSource,
                indication = LocalIndication.current,
            )
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick,
            interactionSource = interactionSource,
        )

        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(weight = 1f),
        )
    }
}