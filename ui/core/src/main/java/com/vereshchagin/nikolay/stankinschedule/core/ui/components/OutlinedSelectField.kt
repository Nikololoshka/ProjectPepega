package com.vereshchagin.nikolay.stankinschedule.core.ui.components

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
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    supportingText: (@Composable () -> Unit)? = null
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
            prefix = prefix,
            suffix = suffix,
            supportingText = supportingText,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExposed) },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            modifier = modifier.menuAnchor()
        )

        ExposedDropdownMenu(
            expanded = isExposed,
            onDismissRequest = { isExposed = false },
            modifier = Modifier.exposedDropdownSize()
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