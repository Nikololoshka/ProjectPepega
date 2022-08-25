package com.vereshchagin.nikolay.stankinschedule.schedule.editor.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import com.vereshchagin.nikolay.stankinschedule.schedule.editor.R
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat

@Composable
fun OutlinedDateField(
    value: String,
    onValueChange: (value: String) -> Unit,
    onCalendarClicked: (date: LocalDate) -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    dateFormat: String = "dd.MM.yyyy",
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        singleLine = true,
        placeholder = {
            Text(text = "dd.mm.yyyy")
        },
        trailingIcon = {
            IconButton(
                onClick = {
                    val current = try {
                        DateTimeFormat.forPattern(dateFormat).parseLocalDate(value)
                    } catch (ignored: Exception) {
                        LocalDate.now()
                    }
                    onCalendarClicked(current)
                }
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_calendar_picker),
                    contentDescription = null
                )
            }
        },
        modifier = modifier
    )
}