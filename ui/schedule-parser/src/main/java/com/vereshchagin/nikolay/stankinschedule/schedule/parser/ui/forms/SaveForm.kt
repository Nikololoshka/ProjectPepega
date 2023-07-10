package com.vereshchagin.nikolay.stankinschedule.schedule.parser.ui.forms

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.vereshchagin.nikolay.stankinschedule.schedule.parser.ui.R
import com.vereshchagin.nikolay.stankinschedule.schedule.parser.ui.model.ParserState

@Composable
fun SaveForm(
    state: ParserState.SaveResult,
    onScheduleNameChanged: (name: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var scheduleName by rememberSaveable { mutableStateOf(state.scheduleName) }

    Column(
        modifier = modifier
    ) {
        OutlinedTextField(
            value = scheduleName,
            onValueChange = { scheduleName = it; onScheduleNameChanged(it) },
            singleLine = true,
            label = { Text(text = stringResource(R.string.schedule_name)) },
            modifier = Modifier.fillMaxWidth()
        )
    }
}