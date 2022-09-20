package com.vereshchagin.nikolay.stankinschedule.schedule.editor.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.vereshchagin.nikolay.stankinschedule.core.ui.theme.Dimen
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.*
import com.vereshchagin.nikolay.stankinschedule.schedule.editor.R
import kotlinx.coroutines.launch


private const val SINGLE_DATE_ID = "single_date"
private const val START_DATE_ID = "start_date"
private const val END_DATE_ID = "end_date"

private const val DATE_FORMAT = "dd.MM.yyyy"

private enum class DateEditorMode {
    SingleMode,
    RangeMode
}

@Composable
fun DateEditorBottomSheet(
    request: DateEditorRequest,
    viewModel: PairEditorViewModel,
    onDismissClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val resources = LocalContext.current.resources

    var currentMode by rememberSaveable { mutableStateOf(DateEditorMode.SingleMode) }
    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }

    var singleDate by rememberSaveable { mutableStateOf("") }

    var startDate by rememberSaveable { mutableStateOf("") }
    var endDate by rememberSaveable { mutableStateOf("") }
    var frequency by rememberSaveable { mutableStateOf(Frequency.EVERY) }

    LaunchedEffect(request) {

        currentMode = DateEditorMode.SingleMode
        errorMessage = null
        singleDate = ""
        startDate = ""
        endDate = ""
        frequency = Frequency.EVERY

        if (request is DateEditorRequest.Edit) {
            when (request.date) {
                is DateSingle -> {
                    currentMode = DateEditorMode.SingleMode
                    singleDate = request.date.toString(DATE_FORMAT)
                }
                is DateRange -> {
                    currentMode = DateEditorMode.RangeMode
                    startDate = request.date.start.toString(DATE_FORMAT)
                    endDate = request.date.end.toString(DATE_FORMAT)
                    frequency = request.date.frequency()
                }
            }
        }
    }

    val scope = rememberCoroutineScope()
    scope.launch {
        viewModel.pickerResults.collect { result ->
            val date = result.date.toString(DATE_FORMAT)
            when (result.id) {
                SINGLE_DATE_ID -> singleDate = date
                START_DATE_ID -> startDate = date
                END_DATE_ID -> endDate = date
            }

            errorMessage = null
        }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
            .padding(Dimen.ContentPadding)
            .padding(bottom = Dimen.ContentPadding)
    ) {

        Text(
            text = stringResource(R.string.editor_date_title),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimen.ContentPadding * 2)
        )

        OutlinedSelectField(
            value = currentMode,
            onValueChanged = {
                currentMode = it
                errorMessage = null
            },
            items = listOf(DateEditorMode.SingleMode, DateEditorMode.RangeMode),
            menuLabel = {
                @StringRes val id = when (it) {
                    DateEditorMode.SingleMode -> R.string.editor_date_single_mode
                    DateEditorMode.RangeMode -> R.string.editor_date_range_mode
                }
                stringResource(id)
            },
            label = {
                Text(text = stringResource(R.string.editor_date_mode))
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = Dimen.ContentPadding)
        )

        errorMessage?.let { error ->
            DateError(
                error = error,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.error,
                        shape = RoundedCornerShape(size = 4.dp)
                    )
                    .padding(16.dp)
            )
        }

        when (currentMode) {
            DateEditorMode.SingleMode -> {
                OutlinedDateField(
                    value = singleDate,
                    onValueChange = {
                        singleDate = it
                        errorMessage = null
                    },
                    label = { Text(text = stringResource(R.string.editor_single_date)) },
                    onCalendarClicked = {
                        viewModel.onDateRequest(
                            DateRequest(
                                title = R.string.editor_select_single_date,
                                selectedDate = it,
                                id = SINGLE_DATE_ID
                            )
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            DateEditorMode.RangeMode -> {
                OutlinedDateField(
                    value = startDate,
                    onValueChange = {
                        startDate = it
                        errorMessage = null
                    },
                    label = { Text(text = stringResource(R.string.editor_start_date)) },
                    onCalendarClicked = {
                        viewModel.onDateRequest(
                            DateRequest(
                                title = R.string.editor_select_start_date,
                                selectedDate = it,
                                id = START_DATE_ID
                            )
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedDateField(
                    value = endDate,
                    onValueChange = {
                        endDate = it
                        errorMessage = null
                    },
                    label = { Text(text = stringResource(R.string.editor_end_date)) },
                    onCalendarClicked = {
                        viewModel.onDateRequest(
                            DateRequest(
                                title = R.string.editor_select_end_date,
                                selectedDate = it,
                                id = END_DATE_ID
                            )
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedSelectField(
                    value = frequency,
                    onValueChanged = {
                        frequency = it
                        errorMessage = null
                    },
                    items = listOf(Frequency.EVERY, Frequency.THROUGHOUT),
                    menuLabel = {
                        when (it) {
                            Frequency.EVERY -> stringResource(R.string.frequency_every_week)
                            Frequency.THROUGHOUT -> stringResource(R.string.frequency_throughout_week)
                            else -> throw IllegalStateException("Invalid frequency: $it")
                        }
                    },
                    label = { Text(text = stringResource(R.string.frequency)) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Button(
            onClick = {
                try {
                    val date: DateItem = if (currentMode == DateEditorMode.SingleMode) {
                        DateSingle(singleDate, DATE_FORMAT)
                    } else {
                        DateRange(startDate, endDate, frequency, DATE_FORMAT)
                    }

                    if (request is DateEditorRequest.Edit) {
                        viewModel.editDate(request.date, date)
                    } else {
                        viewModel.newDate(date)
                    }

                    onDismissClicked()

                } catch (e: Exception) {
                    @StringRes val id: Int? = when (e) {
                        is DateIntersectException -> R.string.editor_impossible_added_date
                        is DateFrequencyException -> R.string.editor_invalid_frequency
                        is DateParseException -> R.string.editor_invalid_date
                        is DateDayOfWeekException -> R.string.editor_invalid_day_of_week
                        else -> null
                    }

                    errorMessage = if (id != null) resources.getString(id) else e.toString()
                }
            },
            enabled = errorMessage == null,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Dimen.ContentPadding * 2)
        ) {
            Text(
                text = if (request is DateEditorRequest.Edit) {
                    stringResource(R.string.editor_edit_date)
                } else {
                    stringResource(R.string.editor_add_date)
                },
            )
        }

        if (request is DateEditorRequest.Edit) {
            TextButton(
                onClick = {
                    viewModel.removeDate(request.date)
                    onDismissClicked()
                }
            ) {
                Text(text = stringResource(R.string.editor_remove_date))
            }
        }
    }
}