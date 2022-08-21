package com.vereshchagin.nikolay.stankinschedule.schedule.ui.viewer.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.vereshchagin.nikolay.stankinschedule.core.ui.components.BackButton
import com.vereshchagin.nikolay.stankinschedule.schedule.R

@Composable
fun ScheduleViewerToolBar(
    scheduleName: String,
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    SmallTopAppBar(
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

        },
        scrollBehavior = scrollBehavior,
        modifier = modifier
    )
}