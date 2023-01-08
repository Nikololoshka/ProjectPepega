package com.vereshchagin.nikolay.stankinschedule.schedule.widget.ui.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.vereshchagin.nikolay.stankinschedule.core.ui.components.BackButton
import com.vereshchagin.nikolay.stankinschedule.schedule.widget.ui.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleWidgetConfigureAppBar(
    onBackPressed: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { Text(text = stringResource(R.string.configure_title)) },
        navigationIcon = { BackButton(onClick = onBackPressed) },
        scrollBehavior = scrollBehavior,
        modifier = modifier
    )
}