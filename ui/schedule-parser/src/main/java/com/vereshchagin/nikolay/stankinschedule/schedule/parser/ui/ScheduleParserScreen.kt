package com.vereshchagin.nikolay.stankinschedule.schedule.parser.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.vereshchagin.nikolay.stankinschedule.core.ui.components.AppScaffold
import com.vereshchagin.nikolay.stankinschedule.core.ui.theme.Dimen
import com.vereshchagin.nikolay.stankinschedule.schedule.parser.ui.components.ParserForm
import com.vereshchagin.nikolay.stankinschedule.schedule.parser.ui.components.ParserState
import com.vereshchagin.nikolay.stankinschedule.schedule.parser.ui.components.ScheduleParserAppBar
import com.vereshchagin.nikolay.stankinschedule.schedule.parser.ui.components.SelectForm
import com.vereshchagin.nikolay.stankinschedule.schedule.parser.ui.components.StepperNavigation


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleParserScreen(
    viewModel: ScheduleParserViewModel,
    onBackPressed: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    val stepState by viewModel.stepState.collectAsState()
    val parserState by viewModel.parserState.collectAsState()

    AppScaffold(
        topBar = {
            ScheduleParserAppBar(
                onBackPressed = onBackPressed,
                scrollBehavior = scrollBehavior
            )
        },
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {

            when (val currentState = parserState) {
                is ParserState.SelectFile -> {
                    SelectForm(
                        state = currentState,
                        selectFile = viewModel::selectFile,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    )
                }

                is ParserState.ParseSchedule -> {
                    ParserForm(
                        state = currentState,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    )
                }
            }

            Divider(modifier = Modifier.fillMaxWidth())

            StepperNavigation(
                stepState = stepState,
                navigateBack = viewModel::back,
                navigateNext = viewModel::next,
                canNext = parserState.isSuccess,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimen.ContentPadding)
            )
        }
    }
}