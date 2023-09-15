package com.vereshchagin.nikolay.stankinschedule.schedule.parser.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.vereshchagin.nikolay.stankinschedule.core.ui.components.AppScaffold
import com.vereshchagin.nikolay.stankinschedule.core.ui.components.TrackCurrentScreen
import com.vereshchagin.nikolay.stankinschedule.core.ui.theme.Dimen
import com.vereshchagin.nikolay.stankinschedule.schedule.parser.ui.components.ScheduleParserAppBar
import com.vereshchagin.nikolay.stankinschedule.schedule.parser.ui.components.StepperNavigation
import com.vereshchagin.nikolay.stankinschedule.schedule.parser.ui.forms.FinishForm
import com.vereshchagin.nikolay.stankinschedule.schedule.parser.ui.forms.ParserForm
import com.vereshchagin.nikolay.stankinschedule.schedule.parser.ui.forms.SaveForm
import com.vereshchagin.nikolay.stankinschedule.schedule.parser.ui.forms.SelectForm
import com.vereshchagin.nikolay.stankinschedule.schedule.parser.ui.forms.SettingsForm
import com.vereshchagin.nikolay.stankinschedule.schedule.parser.ui.model.ParserState


@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun ScheduleParserScreen(
    viewModel: ScheduleParserViewModel,
    onBackPressed: () -> Unit,
    modifier: Modifier = Modifier
) {
    TrackCurrentScreen(screen = "ScheduleParserScreen")

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val parserState by viewModel.parserState.collectAsState()

    BackHandler(
        enabled = parserState.step > 1 && parserState.step < ParserState.STEP_TOTAL
    ) {
        viewModel.back()
    }

    AppScaffold(
        topBar = {
            ScheduleParserAppBar(
                state = parserState,
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

            AnimatedContent(
                targetState = parserState,
                transitionSpec = {
                    when {
                        targetState.step > initialState.step -> {
                            slideInHorizontally { it / 2 } + fadeIn() togetherWith
                                    slideOutHorizontally() + fadeOut()
                        }

                        targetState.step < initialState.step -> {
                            slideInHorizontally() + fadeIn() togetherWith
                                    slideOutHorizontally { it / 2 } + fadeOut()
                        }

                        else -> {
                            fadeIn() togetherWith fadeOut()
                        }
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                label = "Parser"
            ) { currentState ->
                when (currentState) {
                    is ParserState.SelectFile -> {
                        SelectForm(
                            state = currentState,
                            selectFile = viewModel::selectFile,
                            modifier = Modifier
                                .fillMaxWidth()
                                .verticalScroll(state = rememberScrollState())
                                .padding(Dimen.ContentPadding)
                        )
                    }

                    is ParserState.Settings -> {
                        SettingsForm(
                            state = currentState,
                            onSetupSettings = viewModel::onSetupSettings,
                            modifier = Modifier
                                .fillMaxWidth()
                                .verticalScroll(state = rememberScrollState())
                                .padding(Dimen.ContentPadding)
                        )
                    }

                    is ParserState.ParserResult -> {
                        ParserForm(
                            state = currentState,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    is ParserState.SaveResult -> {
                        SaveForm(
                            state = currentState,
                            onScheduleNameChanged = viewModel::onScheduleNameChanged,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(Dimen.ContentPadding)
                        )
                    }

                    is ParserState.ImportFinish -> {
                        FinishForm(
                            state = currentState,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(Dimen.ContentPadding)
                        )
                    }
                }
            }

            Divider(modifier = Modifier.fillMaxWidth())

            StepperNavigation(
                parserState = parserState,
                navigateBack = viewModel::back,
                navigateNext = viewModel::next,
                navigateDone = onBackPressed,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimen.ContentPadding)
            )
        }
    }
}