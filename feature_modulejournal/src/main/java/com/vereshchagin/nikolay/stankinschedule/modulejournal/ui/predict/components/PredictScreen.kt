package com.vereshchagin.nikolay.stankinschedule.modulejournal.ui.predict.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.Card
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material3.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.vereshchagin.nikolay.stankinschedule.core.ui.components.AppScaffold
import com.vereshchagin.nikolay.stankinschedule.core.ui.components.BackButton
import com.vereshchagin.nikolay.stankinschedule.core.ui.theme.Dimen
import com.vereshchagin.nikolay.stankinschedule.modulejournal.R
import com.vereshchagin.nikolay.stankinschedule.modulejournal.domain.model.PredictMark
import com.vereshchagin.nikolay.stankinschedule.modulejournal.ui.predict.PredictViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun PredictScreen(
    viewModel: PredictViewModel,
    modifier: Modifier = Modifier,
    onBackPressed: () -> Unit,
) {
    val semesters by viewModel.semesters.collectAsState()
    val currentSemester by viewModel.currentSemester.collectAsState()

    val bottomState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope()

    AppScaffold(
        topBar = {
            PredictToolBar(
                subTitle = currentSemester,
                onBackPressed = onBackPressed,
                onTitleClicked = { scope.launch { bottomState.show() } }
            )
        },
        modifier = modifier
    ) { innerPadding ->

        ModalBottomSheetLayout(
            sheetState = bottomState,
            sheetContent = {
                SemesterSelectorBottomSheet(
                    currentSemester = currentSemester,
                    semesters = semesters,
                    onSemesterSelected = { semester ->
                        viewModel.changeSemester(semester)
                        scope.launch { bottomState.hide() }
                    },
                    modifier = Modifier
                        .padding(vertical = Dimen.ContentPadding * 2)
                    // .imePadding()
                )
            },
            sheetShape = RoundedCornerShape(
                topStart = 16.0.dp,
                topEnd = 16.0.dp,
                bottomEnd = 0.0.dp,
                bottomStart = 0.0.dp
            ),
            sheetBackgroundColor = MaterialTheme.colorScheme.background,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {

                val predictMarks by viewModel.predictMarks.collectAsState()
                val predictedRating by viewModel.predictedRating.collectAsState()
                val showExposedMarks by viewModel.showExposedMarks.collectAsState()

                PredictDisciplines(
                    predictMarks = predictMarks,
                    onPredictMarkChanged = { item, value ->
                        viewModel.updatePredictMark(item, value)
                    },
                    // contentPadding = PaddingValues(bottom = maxHeight * 0.25f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )

                PredictRatingPanel(
                    predictedRating = predictedRating,
                    showExposedMarks = showExposedMarks,
                    onChangeSemester = { scope.launch { bottomState.show() } },
                    onShowExposedMarks = { viewModel.toggleShowExposedMarks() },
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun PredictToolBar(
    subTitle: String,
    onTitleClicked: () -> Unit,
    onBackPressed: () -> Unit,
) {
    val titleInteractionSource = remember { MutableInteractionSource() }

    TopAppBar(
        title = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize()
                    .clickable(
                        interactionSource = titleInteractionSource,
                        indication = null,
                        onClick = onTitleClicked
                    )
            ) {
                Text(
                    text = stringResource(R.string.predict_title),
                    style = MaterialTheme.typography.titleLarge
                )
                if (subTitle.isNotEmpty()) {
                    Text(
                        text = subTitle,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        navigationIcon = {
            BackButton(
                onClick = onBackPressed
            )
        },
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SemesterSelectorBottomSheet(
    currentSemester: String,
    semesters: List<String>,
    onSemesterSelected: (semester: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
    ) {

        Text(
            text = stringResource(R.string.selected_semester, currentSemester),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = Dimen.ContentPadding * 2)
        )

        semesters.forEach { semester ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(Dimen.ContentPadding),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = semester == currentSemester,
                        onClick = { onSemesterSelected(semester) },
                        role = Role.RadioButton
                    )
                    .padding(Dimen.ContentPadding)
            ) {
                androidx.compose.material3.RadioButton(
                    selected = semester == currentSemester,
                    onClick = null
                )
                Text(
                    text = semester,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class,
    ExperimentalLayoutApi::class)
@Composable
fun PredictDisciplines(
    predictMarks: Map<String, List<PredictMark>>,
    onPredictMarkChanged: (item: PredictMark, value: Int) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    val keyboardController = LocalSoftwareKeyboardController.current


    LazyColumn(
        verticalArrangement = Arrangement.Bottom,
        contentPadding = contentPadding,
        modifier = modifier
    ) {
        predictMarks.forEach { (header, data) ->
            stickyHeader {
                Card(
                    shape = RectangleShape,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = header,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(Dimen.ContentPadding)
                    )
                }
            }
            itemsIndexed(data) { index, item ->
                Row(
                    modifier = Modifier
                ) {

                    val relocationRequester = remember { BringIntoViewRequester() }
                    var focused by remember { mutableStateOf(false) }
                    val imeVisible = WindowInsets.isImeVisible

                    LaunchedEffect(focused) {
                        if (focused) {
                            var done = false
                            while (!done) {
                                if (imeVisible) {
                                    relocationRequester.bringIntoView()
                                    done = true
                                }
                                delay(100L)
                            }
                        }
                    }

                    TextField(
                        value = if (item.value == 0) "" else item.value.toString(),
                        onValueChange = {
                            val value = when {
                                it.isEmpty() -> 0
                                else -> it.toIntOrNull()
                            }
                            if (value != null) {
                                onPredictMarkChanged(item, value)
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = if (index == predictMarks.size - 1) {
                                ImeAction.Done
                            } else {
                                ImeAction.Next
                            }
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { keyboardController?.hide() }
                        ),
                        singleLine = true,
                        modifier = Modifier
                            .bringIntoViewRequester(relocationRequester)
                            .onFocusChanged { focused = it.isFocused }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PredictRatingPanel(
    predictedRating: Float,
    showExposedMarks: Boolean,
    onChangeSemester: () -> Unit,
    onShowExposedMarks: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val currentRating by animateFloatAsState(targetValue = predictedRating)

    Card(
        shape = RectangleShape,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        modifier = modifier
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimen.ContentPadding)
        ) {
            androidx.compose.material3.IconButton(
                onClick = onChangeSemester
            ) {
                Icon(
                    imageVector = Icons.Default.Repeat,
                    contentDescription = null
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = stringResource(R.string.maybe_rating),
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = if (currentRating.isFinite() || currentRating > 0f) {
                        "%.2f".format(currentRating)
                    } else {
                        "--.--"
                    },
                    style = MaterialTheme.typography.titleLarge
                )
            }

            androidx.compose.material3.IconButton(
                onClick = onShowExposedMarks
            ) {
                Icon(
                    painter = painterResource(if (showExposedMarks) {
                        R.drawable.ic_password_visibility
                    } else {
                        R.drawable.ic_password_visibility_off
                    }),
                    contentDescription = null
                )
            }
        }
    }
}
