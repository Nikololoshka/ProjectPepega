package com.vereshchagin.nikolay.stankinschedule.schedule.editor.ui

import androidx.annotation.StringRes
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.vereshchagin.nikolay.stankinschedule.core.ui.theme.Dimen
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.DateRange
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.DateSingle
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.Frequency
import com.vereshchagin.nikolay.stankinschedule.schedule.editor.R
import kotlinx.coroutines.launch
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat


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
    request: DateEditRequest?,
    viewModel: PairEditorViewModel,
) {
    var currentMode by rememberSaveable { mutableStateOf(DateEditorMode.SingleMode) }

    var singleDate by rememberSaveable { mutableStateOf("") }

    var startDate by rememberSaveable { mutableStateOf("") }
    var endDate by rememberSaveable { mutableStateOf("") }
    var frequency by rememberSaveable { mutableStateOf(Frequency.EVERY) }

    LaunchedEffect(request) {

        currentMode = DateEditorMode.SingleMode
        singleDate = ""
        startDate = ""
        endDate = ""
        frequency = Frequency.EVERY

        if (request != null) {
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
        }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(Dimen.ContentPadding)
            .animateContentSize()
    ) {

        Text(
            text = stringResource(R.string.editor_date_title),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimen.ContentPadding * 2)
        )

        OutlinedSelectField(
            value = currentMode,
            onValueChanged = { currentMode = it },
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

        when (currentMode) {
            DateEditorMode.SingleMode -> {
                OutlinedDateField(
                    value = singleDate,
                    onValueChange = { singleDate = it },
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
                    onValueChange = { startDate = it },
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
                    onValueChange = { endDate = it },
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
                    onValueChanged = { frequency = it },
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

            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Dimen.ContentPadding * 2)
        ) {
            Text(
                text = if (request != null) {
                    stringResource(R.string.editor_edit_date)
                } else {
                    stringResource(R.string.editor_add_date)
                },
            )
        }
    }
}