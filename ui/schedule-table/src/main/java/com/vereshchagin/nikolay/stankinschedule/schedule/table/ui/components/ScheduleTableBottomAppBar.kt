package com.vereshchagin.nikolay.stankinschedule.schedule.table.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.vereshchagin.nikolay.stankinschedule.schedule.table.domain.model.TableMode
import com.vereshchagin.nikolay.stankinschedule.schedule.table.ui.R
import org.joda.time.LocalDate

@Composable
fun ScheduleTableBottomAppBar(
    tableMode: TableMode,
    onTableModeChanged: (mode: TableMode) -> Unit,
    page: Int,
    onBackClicked: () -> Unit,
    onNextClicked: () -> Unit,
    onSettingsClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    BottomAppBar(
        modifier = modifier
    ) {
        val isArrowsEnabled by remember(tableMode) {
            derivedStateOf { tableMode == TableMode.Weekly }
        }

        IconButton(
            onClick = onBackClicked,
            enabled = isArrowsEnabled
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_arrow_back),
                contentDescription = null
            )
        }

        Box {
            var showTableModeList by remember { mutableStateOf(false) }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        showTableModeList = true
                    }
            ) {
                Text(
                    text = if (isArrowsEnabled) {
                        stringResource(R.string.table_weekly)
                    } else {
                        stringResource(R.string.table_full)
                    },
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                )

                if (isArrowsEnabled) {
                    Text(
                        text = getDateWeek(page),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }

            DropdownMenu(
                expanded = showTableModeList,
                onDismissRequest = { showTableModeList = false }
            ) {
                DropdownMenuItem(
                    text = { Text(text = stringResource(R.string.table_full)) },
                    onClick = { onTableModeChanged(TableMode.Full); showTableModeList = false }
                )
                DropdownMenuItem(
                    text = { Text(text = stringResource(R.string.table_weekly)) },
                    onClick = { onTableModeChanged(TableMode.Weekly); showTableModeList = false }
                )
            }
        }

        IconButton(
            onClick = onNextClicked,
            enabled = isArrowsEnabled
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_arrow_next),
                contentDescription = null
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        IconButton(onClick = onSettingsClicked) {
            Icon(
                painter = painterResource(R.drawable.ic_table_settings),
                contentDescription = null
            )
        }
    }
}


private fun getDateWeek(page: Int): String {
    val date = LocalDate.now().plusDays(page * 7)
    return date.withDayOfWeek(1).toString("dd.MM.yyyy") +
            "-" +
            date.withDayOfWeek(7).toString("dd.MM.yyyy")
}