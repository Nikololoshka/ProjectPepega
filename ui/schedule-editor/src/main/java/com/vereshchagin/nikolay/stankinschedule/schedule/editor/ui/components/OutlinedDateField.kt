package com.vereshchagin.nikolay.stankinschedule.schedule.editor.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.vereshchagin.nikolay.stankinschedule.schedule.editor.ui.R
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat

@OptIn(ExperimentalMaterial3Api::class)
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