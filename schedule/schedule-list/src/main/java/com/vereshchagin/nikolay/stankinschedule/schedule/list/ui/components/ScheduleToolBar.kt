package com.vereshchagin.nikolay.stankinschedule.schedule.list.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.vereshchagin.nikolay.stankinschedule.schedule.list.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleToolBar(
    onActionMode: () -> Unit,
    modifier: Modifier = Modifier,
    title: String = stringResource(R.string.schedule_list_title),
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge
            )
        },
        actions = {
            IconButton(
                onClick = onActionMode
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_edit),
                    contentDescription = null
                )
            }
        },
        scrollBehavior = scrollBehavior,
        modifier = modifier
    )
}