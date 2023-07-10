package com.vereshchagin.nikolay.stankinschedule.schedule.parser.ui.forms

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.vereshchagin.nikolay.stankinschedule.schedule.parser.ui.R
import com.vereshchagin.nikolay.stankinschedule.schedule.parser.ui.model.ParserState
import com.vereshchagin.nikolay.stankinschedule.schedule.parser.ui.model.SaveScheduleError

@Composable
fun SaveForm(
    state: ParserState.SaveResult,
    onScheduleNameChanged: (name: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var scheduleName by rememberSaveable { mutableStateOf(state.scheduleName) }
    var isShowError by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(state) {
        isShowError = state.saveScheduleError != null
    }

    Column(
        modifier = modifier
    ) {
        OutlinedTextField(
            value = scheduleName,
            onValueChange = {
                scheduleName = it
                isShowError = false
                onScheduleNameChanged(it)
            },
            singleLine = true,
            label = { Text(text = stringResource(R.string.schedule_name)) },
            isError = isShowError,
            supportingText = {
                if (isShowError) {
                    Text(
                        text = when (state.saveScheduleError) {
                            is SaveScheduleError.ScheduleNameAlreadyExists -> stringResource(R.string.name_already_exists)
                            is SaveScheduleError.InvalidScheduleName -> stringResource(R.string.invalid_schedule_name)
                            else -> ""
                        }
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}