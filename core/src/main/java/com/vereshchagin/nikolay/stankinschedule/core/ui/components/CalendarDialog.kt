package com.vereshchagin.nikolay.stankinschedule.core.ui.components

import android.view.ContextThemeWrapper
import android.widget.CalendarView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.vereshchagin.nikolay.stankinschedule.core.R
import org.joda.time.LocalDate
import java.util.*

@Composable
fun CalendarDialog(
    selectedDate: LocalDate = LocalDate.now(),
    minDate: Long? = null,
    maxDate: Long? = null,
    onDateSelected: (date: LocalDate) -> Unit,
    onDismissRequest: () -> Unit,
) {
    var currentDate by remember { mutableStateOf(selectedDate) }

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties()
    ) {
        Column(
            modifier = Modifier
                .wrapContentSize()
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(size = 16.dp)
                )
        ) {
            Column(
                modifier = Modifier
                    .defaultMinSize(minHeight = 72.dp)
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                    )
                    .padding(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.calendar_select_date),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )

                Spacer(modifier = Modifier.size(24.dp))

                Text(
                    text = currentDate.toString("MMM d, yyyy").toTitleCase(),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )

                Spacer(modifier = Modifier.size(16.dp))
            }

            CustomCalendarView(
                selectedDate = selectedDate,
                minDate = minDate,
                maxDate = maxDate,
                onDateSelected = {
                    currentDate = it
                }
            )

            Spacer(modifier = Modifier.size(8.dp))

            Row(
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(bottom = 16.dp, end = 16.dp)
            ) {
                TextButton(
                    onClick = onDismissRequest
                ) {
                    Text(text = stringResource(R.string.cancel))
                }
                TextButton(
                    onClick = { onDateSelected(currentDate) }
                ) {
                    Text(text = stringResource(R.string.ok))
                }
            }
        }
    }
}

private fun String.toTitleCase(locale: Locale = Locale.ROOT): String {
    return replaceFirstChar { if (it.isLowerCase()) it.titlecase(locale) else it.toString() }
}

@Composable
private fun CustomCalendarView(
    selectedDate: LocalDate = LocalDate.now(),
    minDate: Long? = null,
    maxDate: Long? = null,
    onDateSelected: (date: LocalDate) -> Unit,
) {
    AndroidView(
        factory = { context ->
            CalendarView(ContextThemeWrapper(context, R.style.CustomCalendarDialog)).apply {
                date = selectedDate.toDateTimeAtCurrentTime().millis
            }
        },
        update = { view ->
            if (minDate != null) view.minDate = minDate
            if (maxDate != null) view.maxDate = maxDate

            view.setOnDateChangeListener { _, year, month, dayOfMonth ->
                onDateSelected(LocalDate(year, month + 1, dayOfMonth))
            }
        },
        modifier = Modifier.wrapContentSize()
    )
}