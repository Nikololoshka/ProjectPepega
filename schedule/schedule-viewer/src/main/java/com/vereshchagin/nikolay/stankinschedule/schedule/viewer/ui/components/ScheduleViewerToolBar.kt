package com.vereshchagin.nikolay.stankinschedule.schedule.viewer.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.vereshchagin.nikolay.stankinschedule.core.ui.components.BackButton

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