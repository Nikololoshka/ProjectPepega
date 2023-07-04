package com.vereshchagin.nikolay.stankinschedule.schedule.parser.ui.components

import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.unit.dp
import com.vereshchagin.nikolay.stankinschedule.core.ui.theme.Dimen
import com.vereshchagin.nikolay.stankinschedule.schedule.core.ui.components.PairFormatter
import com.vereshchagin.nikolay.stankinschedule.schedule.parser.ui.R
import com.vereshchagin.nikolay.stankinschedule.schedule.table.domain.model.TableConfig
import com.vereshchagin.nikolay.stankinschedule.schedule.table.ui.components.TableView


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ParserForm(
    state: ParserState.ParseSchedule,
    modifier: Modifier = Modifier
) {
    val formatter = remember { PairFormatter() }

    var showSuccessResult by remember { mutableStateOf(false) }
    val successResult by remember(showSuccessResult) {
        derivedStateOf { if (showSuccessResult) state.successResult else emptyList() }
    }

    var showErrorResult by remember { mutableStateOf(false) }
    val errorResult by remember(showErrorResult) {
        derivedStateOf { if (showErrorResult) state.errorResult else emptyList() }
    }

    LazyColumn(
        modifier = modifier
    ) {
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


        stickyHeader(key = "success") {
            Surface(
                onClick = { showSuccessResult = !showSuccessResult },
                shadowElevation = if (showSuccessResult) 3.dp else 0.dp,
                modifier = Modifier.fillParentMaxWidth()
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
                        text = "Success: ${state.successResult.size}",
                        modifier = Modifier.weight(1f)
                    )

                    Icon(
                        painter = if (showSuccessResult) {
                            painterResource(id = R.drawable.ic_expand_less)
                        } else {
                            painterResource(id = R.drawable.ic_expand_more)
                        },
                        contentDescription = null
                    )
                }
            }
        }

        itemsIndexed(
            items = successResult,
            key = { i, _ -> "success $i" }
        ) { index, result ->
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier
                    .fillParentMaxWidth()
                    .padding(Dimen.ContentPadding)
                    .animateItemPlacement(tween())
            ) {
                Text(text = "Data:", style = MaterialTheme.typography.labelSmall)
                Text(text = formatter.format(result.pair))
                Text(text = "Time:", style = MaterialTheme.typography.labelSmall)
                Text(text = result.pair.time.toString())
            }
        }

        stickyHeader(key = "error") {
            Surface(
                onClick = { showErrorResult = !showErrorResult },
                shadowElevation = if (showErrorResult) 3.dp else 0.dp,
                modifier = Modifier.fillParentMaxWidth()
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
                        text = "Errors: ${state.errorResult.size}",
                        modifier = Modifier.weight(1f)
                    )

                    Icon(
                        painter = if (showErrorResult) {
                            painterResource(id = R.drawable.ic_expand_less)
                        } else {
                            painterResource(id = R.drawable.ic_expand_more)
                        },
                        contentDescription = null
                    )
                }
            }
        }

        itemsIndexed(
            items = errorResult,
            key = { i, _ -> "error $i" }
        ) { index, result ->
            Text(
                text = "$index - ${result.error}",
                modifier = Modifier
                    .fillParentMaxWidth()
                    .padding(
                        horizontal = Dimen.ContentPadding,
                        vertical = 4.dp
                    )
                    .animateItemPlacement(tween())
            )
        }
    }
}