package com.vereshchagin.nikolay.stankinschedule.settings.ui.components.color

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.godaddy.android.colorpicker.ClassicColorPicker
import com.vereshchagin.nikolay.stankinschedule.core.utils.parse
import com.vereshchagin.nikolay.stankinschedule.core.utils.toHEX
import com.vereshchagin.nikolay.stankinschedule.settings.R
import com.vereshchagin.nikolay.stankinschedule.core.R as R_core

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorPickerDialog(
    title: String,
    color: Color,
    onColorChanged: (color: Color) -> Unit,
    onDefault: () -> Unit,
    onDismiss: () -> Unit,
) {
    var currentColor by remember { mutableStateOf(color) }

    var currentHex by remember { mutableStateOf(color.toHEX()) }
    var isHexError by remember { mutableStateOf(false) }


    Dialog(
        onDismissRequest = onDismiss,
        content = {
            Card(
                shape = MaterialTheme.shapes.extraLarge
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(24.dp)
                ) {

                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    ColorPicker(
                        color = currentColor,
                        onColorChanged = {
                            currentColor = it
                            currentHex = it.toHEX()
                        },
                        modifier = Modifier
                            .height(200.dp)
                            .fillMaxWidth()
                    )

                    TextField(
                        value = currentHex,
                        isError = isHexError,
                        singleLine = true,
                        leadingIcon = {
                            ColorIcon(color = currentColor)
                        },
                        onValueChange = {
                            try {
                                currentHex = it
                                currentColor = Color.parse(it)
                                isHexError = false

                            } catch (e: IllegalArgumentException) {
                                isHexError = true
                            }
                        }
                    )

                    Row {
                        TextButton(onClick = onDefault) {
                            Text(text = stringResource(R.string.default_color))
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        TextButton(onClick = onDismiss) {
                            Text(text = stringResource(R_core.string.cancel))
                        }
                        TextButton(onClick = { onColorChanged(currentColor) }) {
                            Text(text = stringResource(R_core.string.ok))
                        }
                    }
                }
            }
        }
    )
}


@Composable
private fun ColorPicker(
    color: Color,
    onColorChanged: (color: Color) -> Unit,
    modifier: Modifier = Modifier
) {
    ClassicColorPicker(
        color = color,
        onColorChanged = { onColorChanged(it.toColor()) },
        modifier = modifier,
        showAlphaBar = false
    )
}