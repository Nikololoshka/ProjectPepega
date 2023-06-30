package com.vereshchagin.nikolay.stankinschedule.schedule.parser.ui.components

import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.vereshchagin.nikolay.stankinschedule.core.ui.theme.Dimen
import com.vereshchagin.nikolay.stankinschedule.schedule.parser.ui.R


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ParserForm(
    state: ParserState.ParseSchedule,
    modifier: Modifier = Modifier
) {
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
        stickyHeader(key = "success") {
            Surface(
                onClick = { showSuccessResult = !showSuccessResult },
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
            Text(
                text = "$index - ${result.pair}",
                modifier = Modifier
                    .fillParentMaxWidth()
                    .padding(
                        horizontal = Dimen.ContentPadding,
                        vertical = 4.dp
                    )
                    .animateItemPlacement(tween())
            )
            Divider(modifier = Modifier.fillParentMaxWidth())
        }

        stickyHeader(key = "error") {
            Surface(
                onClick = { showErrorResult = !showErrorResult },
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
            Divider(modifier = Modifier.fillParentMaxWidth())
        }
    }
}