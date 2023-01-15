package com.vereshchagin.nikolay.stankinschedule.schedule.list.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.vereshchagin.nikolay.stankinschedule.core.ui.theme.Dimen
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.ScheduleInfo
import com.vereshchagin.nikolay.stankinschedule.schedule.list.ui.R

@OptIn(ExperimentalFoundationApi::class, ExperimentalAnimationApi::class)
@Composable
fun ScheduleItem(
    schedule: ScheduleInfo,
    isFavorite: Boolean,
    onClicked: () -> Unit,
    onLongClicked: () -> Unit,
    onScheduleFavorite: () -> Unit,
    onScheduleRemove: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimen.ContentPadding),
        modifier = modifier
            .combinedClickable(
                onLongClick = onLongClicked,
                onClick = onClicked
            )
            .padding(
                start = Dimen.ContentPadding * 2f,
                end = Dimen.ContentPadding * 0.5f,
                top = 6.dp,
                bottom = 6.dp
            )
    ) {
        Text(
            text = schedule.scheduleName,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            modifier = Modifier.weight(1f)
        )

        AnimatedVisibility(
            visible = isFavorite,
            enter = scaleIn(),
            exit = scaleOut()
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_favorite),
                contentDescription = null,
                tint = Color.Unspecified,
            )
        }

        var showMenu by remember { mutableStateOf(false) }

        Box {
            IconButton(
                onClick = {
                    showMenu = true
                }
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_more),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            }

            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                DropdownMenuItem(
                    onClick = {
                        onScheduleFavorite()
                        showMenu = false
                    },
                    text = {
                        Text(
                            text = if (isFavorite) {
                                stringResource(R.string.schedule_unset_favorite)
                            } else {
                                stringResource(R.string.schedule_set_favorite)
                            }
                        )
                    }
                )
                DropdownMenuItem(
                    onClick = {
                        onScheduleRemove()
                        showMenu = false
                    },
                    text = {
                        Text(text = stringResource(R.string.schedule_remove_schedule))
                    }
                )
            }
        }
    }
}
