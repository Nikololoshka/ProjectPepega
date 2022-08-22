package com.vereshchagin.nikolay.stankinschedule.schedule.editor.ui

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> OutlinedSelectField(
    value: T,
    onValueChanged: (value: T) -> Unit,
    items: List<T>,
    menuLabel: @Composable (item: T) -> String,
    modifier: Modifier = Modifier,
    label: (@Composable () -> Unit)? = null,
) {

    var isExposed by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = isExposed,
        onExpandedChange = { isExposed = !isExposed },
    ) {
        OutlinedTextField(
            value = menuLabel(value),
            onValueChange = {},
            readOnly = true,
            label = label,
            singleLine = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExposed) },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            modifier = modifier
        )

        ExposedDropdownMenu(
            expanded = isExposed,
            onDismissRequest = { isExposed = false },
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = {
                        Text(text = menuLabel(item))
                    },
                    onClick = {
                        onValueChanged(item)
                        isExposed = false
                    },
                )
            }
        }
    }
}