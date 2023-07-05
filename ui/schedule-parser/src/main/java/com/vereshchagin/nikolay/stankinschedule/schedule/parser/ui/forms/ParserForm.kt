package com.vereshchagin.nikolay.stankinschedule.schedule.parser.ui.forms

import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.vereshchagin.nikolay.stankinschedule.core.ui.theme.Dimen
import com.vereshchagin.nikolay.stankinschedule.schedule.core.ui.components.PairFormatter
import com.vereshchagin.nikolay.stankinschedule.schedule.parser.ui.R
import com.vereshchagin.nikolay.stankinschedule.schedule.parser.ui.model.ParserState
import com.vereshchagin.nikolay.stankinschedule.schedule.table.domain.model.TableConfig
import com.vereshchagin.nikolay.stankinschedule.schedule.table.ui.components.TableView


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ParserForm(
    state: ParserState.ParserResult,
    modifier: Modifier = Modifier
) {
    val formatter = remember { PairFormatter() }

    var showSuccessResult by remember { mutableStateOf(false) }
    val successResult by remember(showSuccessResult) {
        derivedStateOf { if (showSuccessResult) state.successResult else emptyList() }
    }

    var showMissingResult by remember { mutableStateOf(false) }
    val missingResult by remember(showMissingResult) {
        derivedStateOf { if (showMissingResult) state.missingResult else emptyList() }
    }

    var showErrorResult by remember { mutableStateOf(false) }
    val errorResult by remember(showErrorResult) {
        derivedStateOf { if (showErrorResult) state.errorResult else emptyList() }
    }

    LazyColumn(
        modifier = modifier
    ) {
        stickyHeader(key = "table_label") {
            Surface(
                shadowElevation = 3.dp,
                modifier = Modifier.fillParentMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.table_preview),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = Dimen.ContentPadding,
                            vertical = Dimen.ContentPadding * 2
                        )
                )
            }
        }

        item(key = "table") {
            TableView(
                table = state.table,
                tableConfig = TableConfig.default()
                    .copy(color = MaterialTheme.colorScheme.onBackground.toArgb()),
                modifier = Modifier
                    .fillParentMaxWidth()
                    .aspectRatio(1.41f, true)
            )
        }

        stickyExpandHeader(
            isExpand = showSuccessResult,
            labelText = { stringResource(R.string.parse_success, state.successResult.size) },
            onClick = { showSuccessResult = !showSuccessResult },
            key = "success_label"
        )

        itemsIndexed(
            items = successResult,
            key = { i, _ -> "success_$i" }
        ) { _, result ->
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier
                    .fillParentMaxWidth()
                    .padding(Dimen.ContentPadding)
                    .animateItemPlacement(tween())
            ) {
                Text(
                    text = stringResource(R.string.pair_data),
                    style = MaterialTheme.typography.labelSmall
                )
                Text(text = formatter.format(result.pair))
                Text(
                    text = stringResource(R.string.pair_time),
                    style = MaterialTheme.typography.labelSmall
                )
                Text(text = result.pair.time.toString())
            }
        }

        stickyExpandHeader(
            isExpand = showMissingResult,
            labelText = { stringResource(R.string.parse_missing, state.missingResult.size) },
            onClick = { showMissingResult = !showMissingResult },
            key = "missing_label"
        )

        itemsIndexed(
            items = missingResult,
            key = { i, _ -> "missing_$i" }
        ) { _, result ->
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier
                    .fillParentMaxWidth()
                    .padding(Dimen.ContentPadding)
                    .animateItemPlacement(tween())
            ) {
                Text(
                    text = stringResource(R.string.error_context),
                    style = MaterialTheme.typography.labelSmall
                )
                Text(text = result.context)
            }
        }


        stickyExpandHeader(
            isExpand = showErrorResult,
            labelText = { stringResource(R.string.parse_error, state.errorResult.size) },
            onClick = { showErrorResult = !showErrorResult },
            key = "error_label"
        )

        itemsIndexed(
            items = errorResult,
            key = { i, _ -> "error_$i" }
        ) { _, result ->
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier
                    .fillParentMaxWidth()
                    .padding(Dimen.ContentPadding)
                    .animateItemPlacement(tween())
            ) {
                Text(
                    text = stringResource(R.string.error_reason),
                    style = MaterialTheme.typography.labelSmall
                )
                Text(text = result.error)
                Text(
                    text = stringResource(R.string.error_context),
                    style = MaterialTheme.typography.labelSmall
                )
                Text(text = result.context)
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
private fun LazyListScope.stickyExpandHeader(
    isExpand: Boolean,
    labelText: @Composable () -> String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    key: Any? = null,
) {
    stickyHeader(key = key) {
        Surface(
            onClick = onClick,
            shadowElevation = if (isExpand) 3.dp else 0.dp,
            modifier = modifier.fillParentMaxWidth(),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = Dimen.ContentPadding,
                        vertical = Dimen.ContentPadding * 2
                    )
            ) {
                Text(
                    text = labelText(),
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    painter = if (isExpand) {
                        painterResource(id = R.drawable.ic_expand_less)
                    } else {
                        painterResource(id = R.drawable.ic_expand_more)
                    },
                    contentDescription = null
                )
            }
        }
    }
}