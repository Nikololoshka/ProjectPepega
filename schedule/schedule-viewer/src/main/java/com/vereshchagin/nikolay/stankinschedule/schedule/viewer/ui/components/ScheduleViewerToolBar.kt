package com.vereshchagin.nikolay.stankinschedule.schedule.viewer.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.vereshchagin.nikolay.stankinschedule.core.ui.components.BackButton
import com.vereshchagin.nikolay.stankinschedule.schedule.viewer.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleViewerToolBar(
    scheduleName: String,
    onBackClicked: () -> Unit,
    onDayChangeClicked: () -> Unit,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    TopAppBar(
        title = {
            Text(
                text = scheduleName,
                style = MaterialTheme.typography.titleLarge
            )
        },
        navigationIcon = {
            BackButton(onClick = onBackClicked)
        },
        actions = {
            IconButton(
                onClick = onDayChangeClicked
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_date_picker),
                    contentDescription = null
                )
            }
        },
        scrollBehavior = scrollBehavior,
        modifier = modifier
    )
}